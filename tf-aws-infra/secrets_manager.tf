resource "random_password" "db_password" {
  length           = 16
  special          = true
  override_special = "_%#?"
}

resource "aws_secretsmanager_secret" "rds_credentials" {
  name        = "rds-credentials"
  kms_key_id  = aws_kms_key.secrets_kms.arn
  description = "RDS database password for the web app"
}

resource "aws_secretsmanager_secret_version" "rds_credentials_version" {
  secret_id = aws_secretsmanager_secret.rds_credentials.id
  secret_string = jsonencode({
    "username" = var.db_user,
    "password" = random_password.db_password.result
  })
}

output "rds_secret_arn" {
  value = aws_secretsmanager_secret.rds_credentials.arn
}

resource "aws_secretsmanager_secret" "sendgrid_secret" {
  name        = "sendgrid-secret"
  kms_key_id  = aws_kms_key.lambda_key.arn
  description = "SendGrid API Key for email service"
}

resource "aws_secretsmanager_secret_version" "email_service_version" {
  secret_id = aws_secretsmanager_secret.sendgrid_secret.id
  secret_string = jsonencode({
    "sendgrid_api_key" = var.sendgrid_api_key
  })
}
