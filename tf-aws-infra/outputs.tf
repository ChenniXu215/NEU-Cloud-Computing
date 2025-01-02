output "vpc_id" {
  description = "ID of project VPC"
  value       = aws_vpc.csye6225.id
}

output "vpc_arn" {
  description = "ARN of project VPC"
  value       = aws_vpc.csye6225.arn
}

output "public_subnet_ids" {
  description = "The IDs of the public subnets"
  value       = [for s in aws_subnet.csye6225_public : s.id]
}

output "private_subnet_ids" {
  description = "The IDs of the private subnets"
  value       = [for s in aws_subnet.csye6225_private : s.id]
}

output "internet_gateway_id" {
  description = "The ID of the Internet Gateway"
  value       = aws_internet_gateway.csye6225.id
}

output "app_security_group_id" {
  description = "The ID of the application security group"
  value       = aws_security_group.app_security_group.id
}

output "app_security_group_name" {
  description = "The name of the application security group"
  value       = aws_security_group.app_security_group.name
}

# output "ec2_instance_id" {
#   description = "ID of the EC2 instance"
#   value       = aws_instance.app_instance.id
# }

# output "ec2_instance_public_ip" {
#   description = "Public IP address of the EC2 instance"
#   value       = aws_instance.app_instance.public_ip
# }

output "db_security_group_id" {
  description = "The ID of the application security group"
  value       = aws_security_group.app_security_group.id
}

output "db_security_group_name" {
  description = "The name of the application security group"
  value       = aws_security_group.app_security_group.name
}

output "aws_db_parameter_group_id" {
  description = "The ID of the application security group"
  value       = aws_db_parameter_group.rds_parameter_group.id
}

output "aws_db_parameter_group_name" {
  description = "The name of the application security group"
  value       = aws_db_parameter_group.rds_parameter_group.name
}

output "rds_resource_arn" {
  value = aws_db_instance.mysql.arn
}

output "sns_topic_arn" {
  value       = aws_sns_topic.user_verification_topic.arn
  description = "The ARN of the user verification SNS topic"
}
