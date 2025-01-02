# tf-aws-infra
This repository contains the Terraform code for provisioning AWS infrastructure. It allows to easily create and manage resources such as Virtual Private Clouds (VPCs), subnets, Internet Gateways, and more.

## Prerequisites

Before starting, ensure to have the following installed on local machine:

- **Terraform**: Install Terraform from [here](https://www.terraform.io/downloads).
- **AWS CLI**: Install the AWS Command Line Interface (CLI) from [here](https://aws.amazon.com/cli/).
- **AWS Account**: Ensure to have AWS credentials with permissions to provision infrastructure.

### Setup AWS CLI

Configure AWS CLI with credentials:

```bash
aws configure
```

Enter:

AWS Access Key ID
AWS Secret Access Key
Default Region Name (e.g., us-east-1)
Default Output Format (e.g., json)

## Terraform Setup

### Step 1: Clone the Repository
First, clone this repository to local machine

### Step 2: Initialize Terraform
Run the following command to initialize Terraform. This will download the necessary providers and modules.
```bash
terraform init
```

### Step 3: Configure Variables
The infrastructure is configured using several variables that can be adjusted.

### Step 4: Plan the Infrastructure Changes
To see what infrastructure Terraform will create or modify, run the following command:

```bash
terraform plan
```

This command will show a detailed execution plan, outlining what resources will be created, modified, or destroyed.

### Step 5: Apply the Changes
To apply the changes and provision the infrastructure, run:

```bash
terraform apply
```

### Step 6: Verify Infrastructure
After the infrastructure has been provisioned, verify the created resources in the AWS Management Console or using the AWS CLI.

### Step 7: View Outputs
Once the apply command is complete, view important information about infrastructure via the outputs defined in outputs.tf. Run the following command to display the output:

```bash
terraform output
```

### Step 8: Destroy the Infrastructure
To tear down the infrastructure, run the following command:

```bash
terraform destroy
```
This will destroy all the resources that were created by Terraform.

### Securing Web Application Endpoints with SSL Certificates
#### Development Environment
Dev Environment
Use AWS Certificate Manager (ACM) to automatically provision SSL certificates.

Demo Environment
For the demo environment, SSL certificates must be obtained manually from a third-party vendor, such as Namecheap or Let's Encrypt, and imported into AWS Certificate Manager (ACM).

#### Steps to Obtain and Import the SSL Certificate:
Obtain the Certificate: Use a third-party provider like Namecheap or Let's Encrypt. If using Let's Encrypt with Certbot, refer to the Certbot documentation here.

Import the Certificate into ACM: Use the following AWS CLI command to import the certificate into ACM:

```bash
Copy code
aws acm import-certificate \
    --certificate file://path/to/certificate.pem \
    --private-key file://path/to/private-key.pem \
    --certificate-chain file://path/to/certificate-chain.pem \
    --region us-east-1
```
Replace the file paths with the paths to your certificate, private key, and certificate chain.

## GitHub Actions for CI/CD
This repository is integrated with GitHub Actions for Continuous Integration (CI). Whenever a pull request is made, GitHub Actions will:

Run terraform fmt to ensure consistent formatting.
Run terraform validate to validate the configuration.