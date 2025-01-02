packer {
  required_plugins {
    amazon = {
      version = ">= 1.0.0"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "region" {
  type    = string
  default = "us-east-1"
}

variable "instance_type" {
  type    = string
  default = "t2.micro"
}

variable "artifact_path" {
  type    = string
  default = ""
}

source "amazon-ebs" "ubuntu" {
  region        = var.region
  instance_type = var.instance_type
  source_ami    = "ami-0866a3c8686eaeeba"
  ssh_username  = "ubuntu"
  ami_name      = "github-webapp-image-{{timestamp}}"
}

build {
  sources = ["source.amazon-ebs.ubuntu"]

  provisioner "shell" {
    inline = [
      "mkdir -p /home/ubuntu/webapp/",
      "sudo mkdir -p /opt/springboot-app",
      "sudo chown ubuntu:ubuntu /opt/springboot-app"
    ]
  }

  provisioner "file" {
    source      = "${var.artifact_path}"
    destination = "/opt/springboot-app/"
  }

  provisioner "shell" {
    inline = [
      "export DEBIAN_FRONTEND=noninteractive",
      "sudo apt-get update",
      "sudo apt-get install -y openjdk-17-jdk"
    ]
  }

  provisioner "shell" {
    inline = [
      "sudo useradd -r -s /usr/sbin/nologin csye6225",
      "sudo chown -R csye6225:csye6225 /opt/springboot-app/",
      "sudo touch /var/log/healthcheck.log",
      "sudo chown csye6225:csye6225 /var/log/healthcheck.log",

      "sudo tee /etc/systemd/system/springboot.service > /dev/null <<EOT",
      "[Unit]",
      "Description=Spring Boot Application",
      "After=network.target",
      "",
      "[Service]",
      "User=csye6225",
      "Group=csye6225",
      "ExecStart=/usr/bin/java -jar /opt/springboot-app/healthcheck-0.0.1-SNAPSHOT.jar",
      "Environment=\"DB_HOST=DB_HOST_PLACEHOLDER\"",
      "Environment=\"DB_USER=DB_USER_PLACEHOLDER\"",
      "Environment=\"DB_PASS=DB_PASS_PLACEHOLDER\"",
      "Environment=\"DB_NAME=DB_NAME_PLACEHOLDER\"",
      "Environment=\"BUCKET_NAME=BUCKET_NAME_PLACEHOLDER\"",
      "Environment=\"SNS_TOPIC_ARN=SNS_TOPIC_ARN_PLACEHOLDER\"",
      "SuccessExitStatus=143",
      "Restart=always",
      "",
      "[Install]",
      "WantedBy=multi-user.target",
      "EOT",

      "sudo systemctl daemon-reload",
      "sudo systemctl enable springboot.service"
    ]
  }

  provisioner "shell" {
    inline = [
      "sudo apt-get update -y",
      "sudo apt-get install -y curl",
      "curl -O https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb",
      "sudo dpkg -i -E ./amazon-cloudwatch-agent.deb",
      "sudo mkdir -p /opt/aws/amazon-cloudwatch-agent/etc",
      "sudo tee /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json > /dev/null <<EOT",
      "{",
      "  \"metrics\": {",
      "    \"namespace\": \"MyApp/Metrics\",",
      "    \"metrics_collected\": {",
      "      \"cpu\": {",
      "        \"measurement\": [",
      "          \"cpu_usage_idle\",",
      "          \"cpu_usage_iowait\"",
      "        ]",
      "      },",
      "      \"disk\": {",
      "        \"measurement\": [",
      "          \"used_percent\"",
      "        ],",
      "        \"resources\": [\"/\"]",
      "      },",
      "      \"mem\": {",
      "        \"measurement\": [",
      "          \"mem_used_percent\"",
      "        ]",
      "      }",
      "    }",
      "  },",
      "  \"logs\": {",
      "    \"logs_collected\": {",
      "      \"files\": {",
      "        \"collect_list\": [",
      "          {",
      "            \"file_path\": \"/var/log/healthcheck.log\",",
      "            \"log_group_name\": \"MyApp/HealthCheckLogs\",",
      "            \"log_stream_name\": \"{instance_id}/healthcheck\",",
      "            \"timestamp_format\": \"%Y-%m-%d %H:%M:%S\"",
      "          }",
      "        ]",
      "      }",
      "    }",
      "  }",
      "}",
      "EOT",

      "sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json -s",
      "sudo systemctl enable amazon-cloudwatch-agent"
    ]
  }

}