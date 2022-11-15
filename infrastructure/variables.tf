variable "aws_account" {
  type = string
}

variable "environment_name" {
  type = string
}

variable "owner" {
  type = string
}

variable "description" {
  type = string
}

variable "additional_tags" {
  default     = {}
  description = "Additional resource tags"
  type        = map(string)
}

variable "aws_rds_cluster" {
  type = object({
    name                    = string,
    engine                  = string,
    engine_version          = string,
    preffered_backup_time   = string,
    backup_retention_period = number,
    is_storage_encrypted    = bool,
    availability_zones      = set(string)
  })
}
