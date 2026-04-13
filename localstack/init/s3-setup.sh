#!/bin/bash

# read .env file, if it is in the same directory
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

BUCKET_NAME=$AWS_S3_BUCKET_NAME

echo "Configuring S3 bucket: $BUCKET_NAME"

# 1. create bucket
awslocal s3 mb s3://$BUCKET_NAME

# 2. Adjust CORS
awslocal s3api put-bucket-cors --bucket $BUCKET_NAME --cors-configuration '{
  "CORSRules": [
    {
      "AllowedOrigins": ["*"],
      "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
      "AllowedHeaders": ["*"],
      "ExposeHeaders": ["ETag"]
    }
  ]
}'

echo "S3 configuration finished successfully!"