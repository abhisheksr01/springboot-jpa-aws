resource "aws_s3_bucket" "s3_backend" {
  bucket = var.remote_backend_name
  tags = merge(
    var.additional_tags,
    {
      Terraform   = true
      Owner       = var.owner
      Environment = var.environment_name
      Description = var.description
    }
  )
}

resource "aws_s3_bucket_public_access_block" "s3_backend_access_block" {
  bucket = aws_s3_bucket.s3_backend.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "s3_backend_encryption" {
  bucket = aws_s3_bucket.s3_backend.bucket

  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = data.aws_kms_alias.aws_kms_s3_default_key.arn
      sse_algorithm     = "aws:kms"
    }
    bucket_key_enabled = var.s3_bucket_key_enabled
  }
}

resource "aws_s3_bucket_versioning" "s3_backend_versioning" {
  bucket = aws_s3_bucket.s3_backend.id
  versioning_configuration {
    status = var.s3_versioning
  }
}

resource "aws_s3_bucket_acl" "s3_backend_acl" {
  bucket = aws_s3_bucket.s3_backend.id
  acl    = var.acl
}

data "aws_kms_alias" "aws_kms_s3_default_key" {
  name = "alias/aws/s3"
}

resource "aws_dynamodb_table" "tf_state_locks" {
  name         = var.remote_backend_name
  billing_mode = var.dynamodb_billing_mode
  hash_key     = var.dynamodb_hash_key
  point_in_time_recovery {
    enabled = true
  }

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = merge(
    var.additional_tags,
    {
      Terraform   = true
      Owner       = var.owner
      Environment = var.environment_name
      Description = var.description
    },
  )
}
