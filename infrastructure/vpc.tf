module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "~> 3.0"

  name = local.name
  cidr = var.vpc.cidr

  azs              = ["${local.region}a", "${local.region}b", "${local.region}c"]
  public_subnets   = var.vpc.public_subnets
  private_subnets  = var.vpc.private_subnets
  database_subnets = var.vpc.database_subnets

  # Sometimes it is handy to have public access to RDS instances (it is not recommended for production) by specifying these arguments:
  create_database_subnet_group           = var.vpc.create_database_subnet_group
  create_database_subnet_route_table     = var.vpc.create_database_subnet_route_table
  create_database_internet_gateway_route = var.vpc.create_database_internet_gateway_route
  enable_dns_hostnames                   = var.vpc.enable_dns_hostnames
  enable_dns_support                     = var.vpc.enable_dns_support

  tags = local.tags
}

module "security_group" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 4.0"

  name        = local.name
  description = "Complete PostgreSQL example security group"
  vpc_id      = module.vpc.vpc_id

  # ingress
  ingress_with_cidr_blocks = [
    {
      from_port   = var.aws_rds_db.port
      to_port     = var.aws_rds_db.port
      protocol    = "tcp"
      description = "PostgreSQL access from within VPC"
      cidr_blocks = module.vpc.vpc_cidr_block
    },
  ]

  tags = local.tags
}
