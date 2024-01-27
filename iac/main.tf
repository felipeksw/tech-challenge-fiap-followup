terraform {
  required_providers {
    aws = {

      source  = "hashicorp/aws"
      version = "5.21.0"
    }
  }

  required_version = ">= 1.2.0"
}

locals {
  project_name = "tech-challenge-app"
}

provider "aws" {
  region  = var.region
  #profile = "default"  
}
