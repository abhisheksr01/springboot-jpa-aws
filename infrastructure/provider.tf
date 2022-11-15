terraform {
  backend "s3" {}
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.30"
    }

    random = {
      source  = "hashicorp/random"
      version = ">= 2.2"
    }
  }
}
