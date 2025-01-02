resource "aws_vpc" "csye6225" {
  cidr_block = var.vpc_cidr

  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = var.vpc_name
  }

  assign_generated_ipv6_cidr_block = true
}