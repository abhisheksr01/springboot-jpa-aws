variable "aws_account_number" {
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

variable "aws_rds_db" {
  type = object({
    name                                  = string
    region                                = string
    region2                               = string
    engine                                = string
    engine_version                        = string
    family                                = string
    major_engine_version                  = string
    publicly_accessible                   = bool
    instance_class                        = string
    allocated_storage                     = number
    max_allocated_storage                 = number
    port                                  = number
    multi_az                              = bool
    maintenance_window                    = string
    backup_window                         = string
    enabled_cloudwatch_logs_exports       = set(string)
    create_cloudwatch_log_group           = bool
    backup_retention_period               = number
    skip_final_snapshot                   = bool
    deletion_protection                   = bool
    performance_insights_enabled          = bool
    performance_insights_retention_period = number
    create_monitoring_role                = bool
    monitoring_interval                   = number
    monitoring_role_name                  = string
    monitoring_role_use_name_prefix       = bool
    monitoring_role_description           = string
  })
}

variable "vpc" {
  type = object({
    cidr                                   = string
    public_subnets                         = set(string)
    private_subnets                        = set(string)
    database_subnets                       = set(string)
    create_database_subnet_group           = bool
    create_database_subnet_route_table     = bool
    create_database_internet_gateway_route = bool
    create_database_internet_gateway_route = bool
    enable_dns_hostnames                   = bool
    enable_dns_support                     = bool
  })
}
