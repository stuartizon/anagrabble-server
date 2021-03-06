terraform {
  required_version = "~> 0.12.0"
  backend "remote" {
    organization = "anagrabble"
    workspaces {
      prefix = "anagrabble-server-"
    }
  }
}

provider "aws" {
  version = "~> 2.0"
  region = "ap-southeast-2"
}

data "aws_iam_policy_document" "ecs_execution_assume_role_policy" {
  statement {
    actions = [
      "sts:AssumeRole"
    ]

    principals {
      type = "Service"
      identifiers = [
        "ecs-tasks.amazonaws.com"
      ]
    }
  }
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "ecsTaskExecutionRole"
  assume_role_policy = data.aws_iam_policy_document.ecs_execution_assume_role_policy.json
}

resource "aws_iam_role_policy_attachment" "ecs_execution_role" {
  role = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_cloudwatch_log_group" "anagrabble-server" {
  name = "anagrabble-server"
}

resource "aws_ecs_task_definition" "anagrabble-server" {
  family = "anagrabble-server"
  cpu = 512
  memory = 1024
  network_mode = "awsvpc"
  execution_role_arn = aws_iam_role.ecs_execution_role.arn
  requires_compatibilities = [
    "FARGATE"
  ]
  container_definitions = templatefile("${path.module}/task-definition.json", {
    application_version = var.application_version
  })
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnet_ids" "default" {
  vpc_id = data.aws_vpc.default.id
}

data "aws_security_groups" "default" {
  filter {
    name = "vpc-id"
    values = [
      data.aws_vpc.default.id
    ]
  }
}

data "aws_ecs_cluster" "default" {
  cluster_name = "default"
}

data "aws_route53_zone" "anagrabble" {
  name = "anagrabble.com"
}

module "certificate" {
  source = "stuartizon/certificate/aws"
  version = "0.1.2"
  domain_name = "api.anagrabble.com"
  zone_id = data.aws_route53_zone.anagrabble.zone_id
}

resource "aws_lb" "anagrabble-server" {
  name = "anagrabble-server"
  load_balancer_type = "application"
  subnets = data.aws_subnet_ids.default.ids
}

resource "aws_lb_target_group" "anagrabble-server" {
  name = "anagrabble-server"
  port = 8080
  protocol = "HTTP"
  target_type = "ip"
  vpc_id = data.aws_vpc.default.id

  health_check {
    path = "/ping"
    matcher = "200"
  }
}

resource "aws_lb_listener" "anagrabble-server" {
  load_balancer_arn = aws_lb.anagrabble-server.arn
  certificate_arn = module.certificate.arn
  port = 443
  protocol = "HTTPS"
  ssl_policy = "ELBSecurityPolicy-2016-08"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.anagrabble-server.arn
  }
}

resource "aws_ecs_service" "anagrabble-server" {
  name = "anagrabble-server"
  cluster = data.aws_ecs_cluster.default.arn
  task_definition = aws_ecs_task_definition.anagrabble-server.arn
  desired_count = 1
  launch_type = "FARGATE"

  load_balancer {
    target_group_arn = aws_lb_target_group.anagrabble-server.arn
    container_name = "anagrabble-server"
    container_port = 8080
  }

  network_configuration {
    subnets = data.aws_subnet_ids.default.ids
    security_groups = data.aws_security_groups.default.ids
    assign_public_ip = true
  }
}

resource "aws_route53_record" "api" {
  name = "api.anagrabble.com"
  type = "A"
  zone_id = data.aws_route53_zone.anagrabble.zone_id

  alias {
    evaluate_target_health = true
    name = aws_lb.anagrabble-server.dns_name
    zone_id = aws_lb.anagrabble-server.zone_id
  }
}