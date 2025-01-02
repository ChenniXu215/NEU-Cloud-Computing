resource "aws_db_parameter_group" "rds_parameter_group" {
  name        = "my-rds-parameter-group"
  family      = "mysql8.0"
  description = "Custom MySQL parameter group for csye6225"

  parameter {
    name  = "max_connections"
    value = "100"
  }
}
