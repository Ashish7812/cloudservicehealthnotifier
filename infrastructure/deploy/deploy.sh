#!/bin/bash

random_prefix=`head /dev/urandom | tr -dc A-Za-z0-9 | head -c10`
bucket_name=$1
region=$2
stack_name=$3

echo $1, $2, $3

echo "==================================================="
echo "                 UPLOAD THE ARTIFACTS              "
echo "==================================================="

aws s3 cp cloudhealth-service-state-machine.asl.json s3://$bucket_name/cloudhealth-$random_prefix/cloudhealth-service-state-machine.asl.json --region $region
aws s3 cp handlers.zip s3://$bucket_name/cloudhealth-$random_prefix/handlers.zip --region $region
aws s3 cp infrastructure.template.json s3://$bucket_name/cloudhealth-$random_prefix/infrastructure.template.json --region $region

echo "==================================================="
echo "                 ARTIFACTS UPLOADED                "
echo "==================================================="

echo "+++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "                 CREATE THE STACK                  "
echo "+++++++++++++++++++++++++++++++++++++++++++++++++++"

aws cloudformation create-stack --stack-name $stack_name --region $region --capabilities "CAPABILITY_AUTO_EXPAND" "CAPABILITY_NAMED_IAM" "CAPABILITY_IAM" --template-url https://$bucket_name.s3.amazonaws.com/cloudhealth-$random_prefix/infrastructure.template.json --parameters ParameterKey=StackName,ParameterValue=cloudhealth-$random_prefix ParameterKey=Bucket,ParameterValue=$bucket_name ParameterKey=KeyPrefix,ParameterValue=$random_prefix | grep 'StackId' > stack-info.log

stack_id=`awk -F ' ' '{print $2}' stack-info.log`

echo $stack_id

sleep 90s

echo "+++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "                 STACK CREATED                     "
echo "+++++++++++++++++++++++++++++++++++++++++++++++++++"