#!/bin/bash

# Define variables
PACKAGE_DIR="package"
ZIP_FILE="lambda_function.zip"

# Navigate to the script directory
cd $(dirname "$0")

# Remove any existing package directory or zip file
rm -rf $PACKAGE_DIR $ZIP_FILE

# Create a directory for dependencies
mkdir -p $PACKAGE_DIR

# Install dependencies into the package directory
python3 -m pip install pymysql sendgrid -t $PACKAGE_DIR

# Copy the Lambda function code to the package directory
cp lambda_function.py $PACKAGE_DIR/

# Create the zip package
cd $PACKAGE_DIR
zip -r ../$ZIP_FILE .

# Clean up the package directory
cd ..
rm -rf $PACKAGE_DIR

echo "Packaged Lambda function with dependencies as $ZIP_FILE"
