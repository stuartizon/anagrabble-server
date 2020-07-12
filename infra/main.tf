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
  container_definitions = templatefile("${path.module}/task-definition.json", {
    version = var.version
  })
}
