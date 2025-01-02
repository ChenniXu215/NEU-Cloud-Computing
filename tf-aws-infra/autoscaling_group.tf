resource "aws_launch_template" "csye6225_template" {
  name          = "csye6225_asg"
  image_id      = var.ami_id
  instance_type = var.instance_type
  key_name      = var.key_name

  block_device_mappings {
    device_name = "/dev/xvda"

    ebs {
      volume_size           = 20
      volume_type           = "gp2"
      encrypted             = true
      kms_key_id            = aws_kms_key.ec2_kms.arn
      delete_on_termination = true
    }
  }

  network_interfaces {
    associate_public_ip_address = true
    security_groups             = [aws_security_group.app_security_group.id]
  }

  user_data = base64encode(templatefile("${path.module}/user_data.sh", {
    db_host       = aws_db_instance.mysql.endpoint,
    db_user       = var.db_user,
    db_name       = var.db_name,
    bucket_name   = aws_s3_bucket.my_bucket.bucket,
    sns_topic_arn = aws_sns_topic.user_verification_topic.arn
  }))

  iam_instance_profile {
    name = aws_iam_instance_profile.combined_instance_profile_1.name
  }
}

resource "aws_autoscaling_group" "web_app_asg" {
  desired_capacity    = 1
  max_size            = 3
  min_size            = 1
  vpc_zone_identifier = aws_subnet.csye6225_private[*].id

  launch_template {
    id      = aws_launch_template.csye6225_template.id
    version = "$Latest"
  }

  target_group_arns = [aws_lb_target_group.web_app_tg.arn]

  tag {
    key                 = "Name"
    value               = "web-app-instance"
    propagate_at_launch = true
  }
}