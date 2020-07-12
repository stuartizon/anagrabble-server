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
  region  = "ap-southeast-2"
}

resource "null_resource" "hello" {
  provisioner "local-exec" {
    command = "echo hello"
  }
}