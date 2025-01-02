resource "aws_sns_topic" "user_verification_topic" {
  name = "user_verification_topic"
}

resource "aws_sns_topic_subscription" "lambda_subscription" {
  topic_arn = aws_sns_topic.user_verification_topic.arn
  protocol  = "lambda"
  endpoint  = aws_lambda_function.user_verification_lambda.arn
}

resource "aws_lambda_permission" "allow_sns_invoke" {
  statement_id  = "AllowSNSInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.user_verification_lambda.function_name
  principal     = "sns.amazonaws.com"
  source_arn    = aws_sns_topic.user_verification_topic.arn
}

resource "aws_vpc_endpoint" "sns" {
  vpc_id            = aws_vpc.csye6225.id
  service_name      = "com.amazonaws.us-east-1.sns"
  vpc_endpoint_type = "Interface"

  subnet_ids = aws_subnet.csye6225_private[*].id

  security_group_ids = [
    aws_security_group.lambda_sg.id
  ]

  tags = {
    Name = "sns-vpc-endpoint"
  }
}

