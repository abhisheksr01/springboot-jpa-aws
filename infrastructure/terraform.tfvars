owner            = "abhishek"
environment_name = "dev"
description      = "Backend for springbootjpainfra project"
aws_account      = ""
aws_rds_cluster = {
  name                    = "springbootjpa",
  engine                  = "aurora-postgresql",
  engine_version          = "14.5",
  preffered_backup_time   = "07:00-09:00",
  backup_retention_period = 5
  is_storage_encrypted    = true,
  availability_zones      = ["eu-west-2a", "eu-west-2b", "eu-west-2c"]
}
