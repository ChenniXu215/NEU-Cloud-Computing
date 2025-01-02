# resource "aws_instance" "app_instance" {
#   ami           = var.ami_id
#   instance_type = var.instance_type
#   subnet_id     = aws_subnet.csye6225_public[0].id
#   key_name      = var.key_name

#   vpc_security_group_ids = [aws_security_group.app_security_group.id]

#   iam_instance_profile = aws_iam_instance_profile.combined_instance_profile_1.name

#   user_data = templatefile("${path.module}/user_data.sh", {
#     db_host       = aws_db_instance.mysql.endpoint,
#     db_user       = var.db_user,
#     db_name       = var.db_name,
#     bucket_name   = aws_s3_bucket.my_bucket.bucket,
#     sns_topic_arn = aws_sns_topic.user_verification_topic.arn
#   })

#   root_block_device {
#     volume_type = "gp2"
#     volume_size = 25
#   }

#   disable_api_termination = false

#   tags = {
#     Name = "Web Application Instance"
#   }
# }
