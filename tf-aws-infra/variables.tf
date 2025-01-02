variable "aws_region" {
  description = "The AWS region where resources will be created."
  type        = string
}

variable "vpc_name" {
  description = "The name of the VPC"
  type        = string
}


variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for the public subnets."
  type        = list(string)
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for the private subnets."
  type        = list(string)
}

variable "availability_zones" {
  description = "List of availability zones."
  type        = list(string)
}

variable "ami_id" {
  description = "AMI ID for the EC2 instance"
  type        = string
}

variable "instance_type" {
  description = "EC2 instance type"
  default     = "t2.micro"
}

variable "key_name" {
  description = "Key name of the AWS Key Pair to use for the instance"
  type        = string
}

# variable "db_password" {
#   description = "MySQL password"
#   type        = string
# }

variable "db_user" {
  description = "MySQL user"
  type        = string
  default     = "csye6225"
}

variable "db_name" {
  description = "MySQL name"
  type        = string
  default     = "csye6225"
}

variable "dev_zone_id" {
  description = "The Hosted Zone ID for the dev subdomain"
  type        = string
}

variable "zone_name" {
  description = "Domain name for the subdomain"
  type        = string
  default     = "chennicsye6225.me"
}

variable "dev_subdomain_name" {
  description = "The subdomain name for the application"
  type        = string
}

variable "email_source" {
  description = "email source"
  type        = string
  default     = "noreply@em6348.chennicsye6225.me"
}

variable "sendgrid_api_key" {
  type = string
}