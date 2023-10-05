terraform {
  backend "s3" {}
  required_version = "1.5.7"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.30"
    }
  }
}

provider "aws" {
  region = local.region
}

provider "aws" {
  alias  = "region2"
  region = local.region2
}
