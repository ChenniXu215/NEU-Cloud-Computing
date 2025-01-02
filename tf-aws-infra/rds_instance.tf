resource "aws_db_instance" "mysql" {
  allocated_storage      = 20
  engine                 = "mysql"
  instance_class         = "db.t3.micro"
  db_name                = var.db_name
  username               = var.db_user
  password               = random_password.db_password.result
  parameter_group_name   = aws_db_parameter_group.rds_parameter_group.name
  vpc_security_group_ids = [aws_security_group.db_security_group.id]
  multi_az               = false
  publicly_accessible    = false
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  skip_final_snapshot    = true
  storage_encrypted      = true
  kms_key_id             = aws_kms_key.rds_kms.arn
}
