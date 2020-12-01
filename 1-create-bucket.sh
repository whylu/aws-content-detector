#!/bin/bash
BUCKET_ID=$(dd if=/dev/random bs=8 count=1 2>/dev/null | od -An -tx1 | tr -d ' \t\n')
BUCKET_NAME=aws-content-detector-$BUCKET_ID
if [ ! -d "aws-out" ]; then
  mkdir aws-out
fi

echo $BUCKET_NAME > aws-out/bucket-name.txt
aws s3 mb s3://$BUCKET_NAME
