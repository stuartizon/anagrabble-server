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

resource "aws_ecs_task_definition" "anagrabble-server" {
  family = "anagrabble-server"
  cpu = 512
  memory = 1024
  network_mode = "awsvpc"
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
}

resource "aws_lb_listener" "anagrabble-server" {
  load_balancer_arn = aws_lb.anagrabble-server.arn
  port = 80
  protocol = "HTTP"
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
  }
}