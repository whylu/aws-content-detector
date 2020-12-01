#!/bin/bash
set -eo pipefail
if [ ! -d "aws-out" ]; then
  mkdir aws-out
fi

ARTIFACT_BUCKET=$(cat aws-out/bucket-name.txt)
TEMPLATE=template-receiver.yml

aws cloudformation package --template-file $TEMPLATE --s3-bucket $ARTIFACT_BUCKET --output-template-file aws-out/receiver-out.yml
aws cloudformation deploy --template-file aws-out/receiver-out.yml --stack-name content-detector --capabilities CAPABILITY_NAMED_IAM
