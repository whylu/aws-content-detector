

# Content Detector

create a bucket for this project
```sh
./1-create-bucket.sh
```

## Order Receiver

- create table
```sql
CREATE TABLE IF NOT EXISTS submit_order (
	id serial PRIMARY KEY,
	content varchar(1024) NOT NULL,
	submit_time integer NOT NULL, 
	is_danger boolean default false
)
```

### Build
- build
```sh
mvn clean package
```
- test local
edit sam-env.json
```json
{
	"ReceiverFunction": {
		"DB_URL": "jdbc:postgresql://<host>:<port>/<database:postgres>",
		"DB_USERNAME": "<db_username>",
		"DB_PASSWORD": "<password>"
	}
}
```
use sam local invoke
```sh
sam local invoke \
  -e event-submit-order.json \
  --template template-receiver-local.yml \
  --env-vars sam-env.json \
  ReceiverFunction
```
sam-env.json will inject to template-receiver.yml


### Deploy
edit template-receiver.yml
```yml
      Environment:
        Variables:
          DB_URL: jdbc:postgresql://<host>:<port>/<database:postgres>
          DB_USERNAME: <db_username>
          DB_PASSWORD: <password>

      VpcConfig:
        SecurityGroupIds:
          SecurityGroupIds:
            - sg-XXXXXXXX
          SubnetIds:
            - subnet-XXXXXXXX
            - subnet-XXXXXXXX
            - subnet-XXXXXXXX
```

then depoly
```sh
./2-deploy-receiver.sh
```

test receiver endpoint
```sh
curl -X POST --data 'this is raw data' <api-url>
```


by current step, we have used this service in aws: 
- Lambda: java lambda function 
- RDS: lambda function access DB
- API gateway: rest API endpoint to lambda
- IAM: permission of roles
- S3: to upload lambda code
- CloudFormation: deploy lambda function
- VPC: network control between lambda and RDS
- CloudWatch: for logs




```sql

CREATE TABLE IF NOT EXISTS detect_history (
  id serial PRIMARY KEY,
  order_id integer not null, 
  strategy varchar(512) NOT NULL,
  found_suspicion boolean default false,
  process_time integer NOT NULL
)

create index if not exists idx_order_id on detect_history(order_id);
```


-- Auto-generated SQL script #202012020925
INSERT INTO public.detect_history (order_id,strategy,found_suspicion)  VALUES (32,'ming.test.detector.HelloDetector',false);


use sam local invoke
```sh
sam local invoke \
  -e event-sns-new-order.json \
  --template template-receiver-local.yml \
  --env-vars sam-env.json \
  ContentDetector
```
