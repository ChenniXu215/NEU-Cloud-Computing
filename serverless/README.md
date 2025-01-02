# serverless

## Overview

This Lambda function handles email verification for a web application. It processes messages from an Amazon SNS topic, generates a verification link, and sends an email using SendGrid. The function also includes error handling for common scenarios.

---

## Features

1. **Integration with AWS SNS**:
   - Listens to messages published to an SNS topic.
   - Parses the message to retrieve the user's email and verification UUID.

2. **Email Sending with SendGrid**:
   - Generates a verification link with a specified format.
   - Sends the email using the SendGrid API.

3. **Environment Variables**:
   - Supports customization via environment variables for various configurations:
     - **`RDS_ENDPOINT`**: MySQL RDS endpoint.
     - **`DB_USER`**: Database username.
     - **`DB_PASSWORD`**: Database password.
     - **`DB_NAME`**: Database name.
     - **`EMAIL_SOURCE`**: Email address used as the sender.
     - **`SENDGRID_API_KEY`**: SendGrid API key for sending emails.

4. **Expiration Logic**:
   - The generated verification link is valid for a configurable duration (default: 2 minutes).

---

## Prerequisites

### AWS Resources:
- **SNS Topic**: Must be configured to trigger this Lambda function.
- **IAM Role**: Ensure the Lambda execution role has:
  - Permission to process messages from SNS.
  - Necessary network access for external services.

### SendGrid:
- Verify your domain and set up DNS records.
- Generate and store the SendGrid API key in an environment variable.

---

## Key Files

### `lambda_function.py`
This is the main Lambda function file. Key components:
- **Handler Function**: `lambda_handler`
  - Processes incoming SNS messages.
  - Calls `send_verification_email` to send the email.
- **Helper Function**: `send_verification_email`
  - Constructs the email content and sends it using SendGrid.

---

## Usage

### Environment Variables
Before deploying the Lambda function, ensure the following environment variables are set:
- `RDS_ENDPOINT`
- `DB_USER`
- `DB_PASSWORD`
- `DB_NAME`
- `EMAIL_SOURCE`
- `SENDGRID_API_KEY`

### Deployment Steps
1. **Package the Lambda Function**:
   - Use a packaging script to include dependencies and compress the function into a `.zip` file.

2. **Upload to AWS Lambda**:
   - Deploy the `.zip` package to the AWS Lambda console.

3. **Configure the Trigger**:
   - Attach the SNS topic as the trigger for the Lambda function.

---

## Testing

### Local Testing
- Mock an SNS message payload to simulate an actual event.
- Use local SendGrid credentials to verify email functionality.

### AWS Testing
1. Publish a message to the SNS topic linked to the Lambda function.
2. Check the Lambda execution logs in CloudWatch for debugging information.

---

## Logs
- The function uses `print` statements for logging, which are available in AWS CloudWatch.
- Common log messages include:
  - `Sending verification email to...`
  - `SendGrid Response: <status_code>`

---

## Error Handling
- **Invalid Event Structure**: Throws a `ValueError` if the expected keys are missing.
- **Email Sending Errors**: Captures and logs any issues with SendGrid API calls.
- **General Errors**: Logs unexpected exceptions and returns a 500 status code.

---

## Example
### Sample SNS Message
```json
{
  "Records": [
    {
      "Sns": {
        "Message": "{\"email\": \"user@example.com\", \"verification_uuid\": \"1234-5678-91011\"}"
      }
    }
  ]
}
