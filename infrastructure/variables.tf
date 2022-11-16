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
  default = {
    allocated_storage                     = 1
    backup_retention_period               = 1
    backup_window                         = "value"
    create_cloudwatch_log_group           = false
    create_monitoring_role                = false
    deletion_protection                   = false
    enabled_cloudwatch_logs_exports       = ["value"]
    engine                                = "value"
    engine_version                        = "value"
    family                                = "value"
    instance_class                        = "value"
    major_engine_version                  = "value"
    manumberenance_window                 = "value"
    max_allocated_storage                 = 1
    monitoring_numbererval                = 1
    monitoring_role_description           = "value"
    monitoring_role_name                  = "value"
    monitoring_role_use_name_prefix       = false
    multi_az                              = false
    name                                  = "value"
    region                                = "eu-west-2",
    region2                               = "eu-west-1",
    performance_insights_enabled          = false
    performance_insights_retention_period = 1
    port                                  = 1
    skip_final_snapshot                   = false
  }


  type = object({
    name                                  = string,
    region                                = string,
    region2                               = string,
    engine                                = string,
    engine_version                        = string,
    family                                = string,
    major_engine_version                  = string,
    instance_class                        = string,
    allocated_storage                     = number,
    max_allocated_storage                 = number,
    port                                  = number,
    multi_az                              = bool,
    manumberenance_window                 = string,
    backup_window                         = string,
    enabled_cloudwatch_logs_exports       = set(string),
    create_cloudwatch_log_group           = bool,
    backup_retention_period               = number,
    skip_final_snapshot                   = bool,
    deletion_protection                   = bool,
    performance_insights_enabled          = bool,
    performance_insights_retention_period = number,
    create_monitoring_role                = bool,
    monitoring_numbererval                = number,
    monitoring_role_name                  = string,
    monitoring_role_use_name_prefix       = bool,
    monitoring_role_description           = string
  })
}

variable "vpc" {
  default = {
    cidr                                      = "value"
    create_database_numberernet_gateway_route = false
    create_database_subnet_group              = false
    create_database_subnet_route_table        = false
    database_subnets                          = ["value"]
    enable_dns_hostnames                      = false
    enable_dns_support                        = false
    private_subnets                           = ["value"]
    public_subnets                            = ["value"]
  }
  type = object({
    cidr                                      = string
    public_subnets                            = set(string)
    private_subnets                           = set(string)
    database_subnets                          = set(string)
    create_database_subnet_group              = bool
    create_database_subnet_route_table        = bool
    create_database_numberernet_gateway_route = bool
    enable_dns_hostnames                      = bool
    enable_dns_support                        = bool
  })
}
