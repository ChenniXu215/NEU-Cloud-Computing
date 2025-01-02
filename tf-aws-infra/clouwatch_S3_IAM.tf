resource "aws_iam_role" "combined_role" {
  name = "combined_cloudwatch_s3_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_policy" "dns_policy" {
  name        = "dns_policy"
  description = "Policy allowing DNS access for EC2"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "route53:GetHostedZone",
          "route53:ChangeResourceRecordSets",
          "route53:ListHostedZones",
          "route53:ListResourceRecordSets"
        ],
        Resource = "arn:aws:route53:::hostedzone/*"
      }
    ]
  })
}

resource "aws_iam_policy" "cloudwatch_agent_policy" {
  name        = "cloudwatch_agent_policy"
  description = "Policy allowing CloudWatch access for EC2"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:DescribeLogStreams",
          "logs:DescribeLogGroups",
          "ssm:GetParameter",
          "cloudwatch:PutMetricData",
          "cloudwatch:GetMetricData",
          "cloudwatch:ListMetrics"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_policy" "s3_image_storage_policy" {
  name        = "S3ImageStoragePolicy"
  description = "Policy for Spring Boot application to store images in S3"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:ListBucket"
        ],
        Resource = "arn:aws:s3:::${aws_s3_bucket.my_bucket.bucket}"
      },
      {
        Effect = "Allow",
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject"
        ],
        Resource = "arn:aws:s3:::${aws_s3_bucket.my_bucket.bucket}/*"
      }
    ]
  })
}

resource "aws_iam_policy" "sns_policy" {
  name = "instance_policy"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents",
        ],
        Resource = "*"
      },
      {
        Effect = "Allow",
        Action = [
          "sns:*"
        ],
        Resource = aws_sns_topic.user_verification_topic.arn
      }
    ]
  })
}

resource "aws_iam_policy" "ec2_policy" {
  name = "ec2-policy"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "ec2:CreateNetworkInterface",
          "ec2:DescribeNetworkInterfaces",
          "ec2:DeleteNetworkInterface",
          "ec2:AttachNetworkInterface",
          "ec2:AssignPrivateIpAddresses"
        ],
        Resource = "*"
      },
      {
        Sid    = "AllowKMSDecryption",
        Effect = "Allow",
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:ReEncrypt*",
          "kms:GenerateDataKey*",
          "kms:DescribeKey"
        ],
        Resource = aws_kms_key.ec2_kms.arn
      }
    ]
  })
}

resource "aws_iam_policy" "kms_policy" {
  name        = "kms_policy"
  description = "Policy allowing access to all required KMS keys"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid : "AllowKMSAccessForS3",
        Effect = "Allow",
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey",
          "kms:DescribeKey"
        ],
        Resource = aws_kms_key.s3_kms.arn
      },
      {
        Sid : "AllowKMSAccessForRDS",
        Effect = "Allow",
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey",
          "kms:DescribeKey"
        ],
        Resource = aws_kms_key.rds_kms.arn
      },
      {
        Sid : "AllowKMSAccessForSecrets",
        Effect = "Allow",
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey",
          "kms:DescribeKey"
        ],
        Resource = aws_kms_key.secrets_kms.arn
      },
      {
        Sid : "AllowKMSAccessForSecretsLambda",
        Effect = "Allow",
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey",
          "kms:DescribeKey"
        ],
        Resource = aws_kms_key.lambda_key.arn
      },
      {
        Sid : "AllowKMSAccessForEC2",
        Effect = "Allow",
        Action = [
          "kms:Encrypt",
          "kms:Decrypt",
          "kms:GenerateDataKey",
          "kms:DescribeKey"
        ],
        Resource = aws_kms_key.ec2_kms.arn
      },
      {
        Sid : "AllowSecretsManagerAccess",
        Effect = "Allow",
        Action = [
          "secretsmanager:GetSecretValue"
        ],
        Resource = [
          aws_secretsmanager_secret.rds_credentials.arn,
          aws_secretsmanager_secret.sendgrid_secret.arn
        ]
      },
      {
        Sid : "AllowEC2NetworkInterfaceActions",
        Effect = "Allow",
        Action = [
          "ec2:CreateNetworkInterface",
          "ec2:DescribeNetworkInterfaces",
          "ec2:DeleteNetworkInterface",
          "ec2:AttachNetworkInterface"
        ],
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_kms_policy" {
  role       = aws_iam_role.combined_role.name
  policy_arn = aws_iam_policy.kms_policy.arn
}

resource "aws_iam_role_policy_attachment" "attach_ec2_policy" {
  role       = aws_iam_role.combined_role.name
  policy_arn = aws_iam_policy.ec2_policy.arn
}

resource "aws_iam_role_policy_attachment" "attach_dns_policy" {
  role       = aws_iam_role.combined_role.name
  policy_arn = aws_iam_policy.dns_policy.arn
}

resource "aws_iam_role_policy_attachment" "attach_cloudwatch_policy" {
  role       = aws_iam_role.combined_role.name
  policy_arn = aws_iam_policy.cloudwatch_agent_policy.arn
}

resource "aws_iam_role_policy_attachment" "attach_s3_policy" {
  role       = aws_iam_role.combined_role.name
  policy_arn = aws_iam_policy.s3_image_storage_policy.arn
}

resource "aws_iam_role_policy_attachment" "attach_sns_policy" {
  role       = aws_iam_role.combined_role.name
  policy_arn = aws_iam_policy.sns_policy.arn
}

resource "aws_iam_instance_profile" "combined_instance_profile_1" {
  name = "combined_instance_profile_1"
  role = aws_iam_role.combined_role.name
}
