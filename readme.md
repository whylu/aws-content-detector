

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
- Lambda
- RDS
- API gateway
- IAM
- CloudFormation
- VPC
- CloudWatch

