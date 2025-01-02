resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "rds-subnet-group"
  subnet_ids = aws_subnet.csye6225_private[*].id

  tags = {
    Name = "RDS Subnet Group"
  }
}
