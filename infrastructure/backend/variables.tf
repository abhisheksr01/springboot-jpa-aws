variable "remote_backend_name" {
  type = string
}

variable "default_tags" {
  type = object({
    Owner       = string
    Team        = string
    Environment = string
    Description = string
    Repository  = string
    Provisioner = string
  })
}
