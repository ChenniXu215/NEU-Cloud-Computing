resource "aws_security_group" "lambda_sg" {
  name   = "lambda-security-group"
  vpc_id = aws_vpc.csye6225.id

  ingress {
    from_port = 3306
    to_port   = 3306
    protocol  = "tcp"
    security_groups = [
      aws_security_group.db_security_group.id
    ]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
