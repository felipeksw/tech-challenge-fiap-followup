variable "region" {
  default     = "us-east-1"
  description = "AWS region"
}

variable "tag" {
  default = {
    Terraform = "true"
    Project   = "tech-challenge-app"
  }
  description = "AWS tag"
  type        = map(string)
}
