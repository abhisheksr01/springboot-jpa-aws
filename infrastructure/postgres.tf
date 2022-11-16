module "db" {
  source  = "terraform-aws-modules/rds/aws"
  version = "5.1.1"

  identifier = local.name
  #   Enable only for local testing or learning purpose, default value is false and is a recommended configuration.
  #   publicly_accessible  = true
  #   All available versions: https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_PostgreSQL.html#PostgreSQL.Concepts
  engine               = var.aws_rds_db.name
  engine_version       = var.aws_rds_db.engine_version
  family               = var.aws_rds_db.family               # DB parameter group
  major_engine_version = var.aws_rds_db.major_engine_version # DB option group
  instance_class       = var.aws_rds_db.instance_class

  allocated_storage     = var.aws_rds_db.allocated_storage
  max_allocated_storage = var.aws_rds_db.max_allocated_storage

  db_name  = "completePostgresql"
  username = "complete_postgresql"
  port     = var.aws_rds_db.port

  multi_az               = var.aws_rds_db.multi_az
  db_subnet_group_name   = module.vpc.database_subnet_group
  vpc_security_group_ids = [module.security_group.security_group_id]

  maintenance_window              = var.aws_rds_db.maintenance_window
  backup_window                   = var.aws_rds_db.backup_window
  enabled_cloudwatch_logs_exports = var.aws_rds_db.enabled_cloudwatch_logs_exports
  create_cloudwatch_log_group     = var.aws_rds_db.create_cloudwatch_log_group

  backup_retention_period = var.aws_rds_db.backup_retention_period
  skip_final_snapshot     = var.aws_rds_db.skip_final_snapshot
  deletion_protection     = var.aws_rds_db.deletion_protection

  performance_insights_enabled          = var.aws_rds_db.performance_insights_enabled
  performance_insights_retention_period = var.aws_rds_db.performance_insights_retention_period
  create_monitoring_role                = var.aws_rds_db.create_monitoring_role
  monitoring_interval                   = var.aws_rds_db.monitoring_interval
  monitoring_role_name                  = var.aws_rds_db.monitoring_role_name
  monitoring_role_use_name_prefix       = var.aws_rds_db.monitoring_role_use_name_prefix
  monitoring_role_description           = var.aws_rds_db.monitoring_role_description

  parameters = [
    {
      name  = "autovacuum"
      value = 1
    },
    {
      name  = "client_encoding"
      value = "utf8"
    }
  ]

  tags = local.tags
  db_option_group_tags = {
    "Sensitive" = "low"
  }
  db_parameter_group_tags = {
    "Sensitive" = "low"
  }
}

################################################################################
# RDS Automated Backups Replication Module
################################################################################
module "kms" {
  source      = "terraform-aws-modules/kms/aws"
  version     = "~> 1.0"
  description = "KMS key for cross region automated backups replication"

  # Aliases
  aliases                 = [local.name]
  aliases_use_name_prefix = true

  key_owners = [data.aws_caller_identity.current.arn]

  tags = local.tags

  providers = {
    aws = aws.region2
  }
}

module "db_automated_backups_replication" {
  # source = "../modules/db_instance_automated_backups_replication"
  source = "./modules/db_instance_automated_backups_replication"

  source_db_instance_arn = module.db.db_instance_arn
  kms_key_arn            = module.kms.key_arn

  providers = {
    aws = aws.region2
  }
}
