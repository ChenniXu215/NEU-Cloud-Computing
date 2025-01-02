#!/bin/bash
sudo apt update -y

sudo apt install -y unzip curl jq

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

aws --version || { echo "AWS CLI installation failed" ; exit 1; }

DB_SECRET=$(aws secretsmanager get-secret-value --secret-id rds-credentials --query 'SecretString' --output text)
DB_PASS=$(echo $DB_SECRET | jq -r '.password')

DB_HOST="${db_host}"
DB_USER="${db_user}"
DB_NAME="${db_name}"
BUCKET_NAME="${bucket_name}"
SNS_TOPIC_ARN="${sns_topic_arn}"

echo "export DB_HOST=$DB_HOST" >> /etc/environment
echo "export DB_USER=$DB_USER" >> /etc/environment
echo "export DB_PASS=$DB_PASS" >> /etc/environment
echo "export DB_NAME=$DB_NAME" >> /etc/environment
echo "export BUCKET_NAME=$BUCKET_NAME" >> /etc/environment
echo "export SNS_TOPIC_ARN=$SNS_TOPIC_ARN" >> /etc/environment

sudo sed -i "s/DB_HOST_PLACEHOLDER/$DB_HOST/" /etc/systemd/system/springboot.service
sudo sed -i "s/DB_USER_PLACEHOLDER/$DB_USER/" /etc/systemd/system/springboot.service
sudo sed -i "s/DB_PASS_PLACEHOLDER/$DB_PASS/" /etc/systemd/system/springboot.service
sudo sed -i "s/DB_NAME_PLACEHOLDER/$DB_NAME/" /etc/systemd/system/springboot.service
sudo sed -i "s/BUCKET_NAME_PLACEHOLDER/$BUCKET_NAME/" /etc/systemd/system/springboot.service
sudo sed -i "s/SNS_TOPIC_ARN_PLACEHOLDER/$SNS_TOPIC_ARN/" /etc/systemd/system/springboot.service

sudo systemctl daemon-reload
sudo systemctl start springboot.service

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c ssm:AmazonCloudWatch-linux -s
sudo systemctl restart amazon-cloudwatch-agent
