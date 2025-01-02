
import json
import boto3
import os
from datetime import datetime, timedelta
from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail

secrets_client = boto3.client('secretsmanager')

sns_client = boto3.client('sns')
ses_client = boto3.client('ses')

email_source = os.getenv('EMAIL_SOURCE')

expiration_minutes = 2

def get_secret(secret_name):
    """Fetch the secret from Secrets Manager."""
    response = secrets_client.get_secret_value(SecretId=secret_name)
    return json.loads(response['SecretString'])

def lambda_handler(event, context):
    try:
        email_secret = get_secret("sendgrid-secret")
        sendgrid_api_key = email_secret["sendgrid_api_key"]

        if "Records" not in event or not event["Records"]:
            raise ValueError("Invalid event structure: 'Records' key is missing or empty.")

        message = json.loads(event['Records'][0]['Sns']['Message'])
        email = message['email']
        verification_uuid = message['verification_uuid']

        if not email or not verification_uuid:
            raise ValueError("Missing required fields in the message: 'email' or 'verification_uuid'.")


        verification_link = f"https://demo.chennicsye6225.me/verify?email={email}&uuid={verification_uuid}"

        send_verification_email(email, verification_link, sendgrid_api_key)

        return {
            'statusCode': 200,
            'body': json.dumps('Email sent successfully.')
        }
    except Exception as e:
        print(e)
        return {
            'statusCode': 500,
            'body': json.dumps('Error processing request.')
        }

def send_verification_email(email, verification_link, sendgrid_api_key):
    print(f"Sending verification email to: {email} with link: {verification_link}")
    try:
        message = Mail(
            from_email=email_source,
            to_emails=email,
            subject='Verify your email',
            plain_text_content=f'Please verify your email by clicking the link: {verification_link}. This link will expire in 2 minutes.'
        )
        sg = SendGridAPIClient(sendgrid_api_key)
        response = sg.send(message)
        print(f"SendGrid Response: {response.status_code}")
        if response.status_code not in [200, 202]:
            raise ValueError("Error sending email through SendGrid")
    except Exception as e:
        print(f"Error sending email: {e}")
        raise
