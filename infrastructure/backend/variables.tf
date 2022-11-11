variable "remote_backend_name" {
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

variable "s3_versioning" {
  type = string
}


variable "acl" {
  type    = string
  default = "private"
}

variable "sse_algorithm" {
  type    = string
  default = "AES256"
}

variable "dynamodb_billing_mode" {
  type    = string
  default = "PAY_PER_REQUEST"
}

variable "dynamodb_hash_key" {
  type    = string
  default = "LockID"
}

variable "additional_tags" {
  default     = {}
  description = "Additional resource tags"
  type        = map(string)
}

variable "s3_bucket_key_enabled" {
  default     = true
  description = "Enable S3 bucket for KMS key"
  type        = bool
}
