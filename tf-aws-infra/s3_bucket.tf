locals {
  bucket = uuid()
}

resource "aws_s3_bucket" "my_bucket" {
  bucket = local.bucket

  force_destroy = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "my_bucket_encryption" {
  bucket = aws_s3_bucket.my_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = aws_kms_key.s3_kms.arn
    }
  }
}

resource "aws_s3_bucket_versioning" "my_bucket_versioning" {
  bucket = aws_s3_bucket.my_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "my_bucket_lifecycle" {
  bucket = aws_s3_bucket.my_bucket.id

  rule {
    id     = "transition-to-standard-ia"
    status = "Enabled"

    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }
}


resource "aws_s3_bucket_policy" "my_bucket_policy" {
  bucket = aws_s3_bucket.my_bucket.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = { "AWS" = aws_iam_role.combined_role.arn },
        Action    = ["s3:ListBucket"],
        Resource  = ["${aws_s3_bucket.my_bucket.arn}"]
      },
      {
        Effect    = "Allow",
        Principal = { "AWS" = aws_iam_role.combined_role.arn },
        Action    = ["s3:GetObject", "s3:PutObject", "s3:DeleteObject"],
        Resource  = ["${aws_s3_bucket.my_bucket.arn}/*"]
    }]
  })
}
