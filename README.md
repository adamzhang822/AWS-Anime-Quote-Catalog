# stackFinal Integration Instructions

## vaadinApp
This is the vaadin app for Anime quotes catalog. You can perform CRUD to manage quote bookmarks

### local
To run this project locally you can either run in development mode from IntelliJ, or packing into
a docker container and run on a tomcat server using the following commands:


```bash
mvn clean package -Pproduction
```

To build the docker image:

```bash
docker build -t [your-docker-hub-id]/vaadin-books .
```

### remote
Once you have a docker image, you may deploy to AWS Elastic Beanstalk. Please choose Tomcat as the platform.
Do not attempt to deploy on Lightsail.
Make sure you change Cognito redirect address in application.properties as well as on Cognito console in AWS

## mongoDB
This is the persistence layer for Anime Quotes catalog where users store their favorites

### local
Run in a docker container:

```bash
docker run -ti --rm -p 27107:27107 mongo:4.0
```

### remote
Deploy to a Docker container on Lightsail

## quarkusRest
This is the backend REST API for doing CRUD to manage each user's favorite Anime quotes

### local
Make sure your mongoDB container is running as instructed in previous section.
You can set up the quarkus backend by directly running the quarkuRest project in your IDE.
Alternatively, you can also build a docker image and run locally.

To build the docker image (platform option only needed if you are running on M1 mac):

``` bash
docker build -f src/main/docker/Dockerfile.jvm -t [your-docker-hub-id]/quarkus-rest --platform=linux/amd64 .
```

### remote
Deploy to a Docker container on Lightsail

## awsContact
This project sets up the AWS Lambda allowing users to send email to the website owner using AWS SES (Simple Email Service)
To build your own app, please set up your own AWS contact and set up SES to send and receive emails using your own personal email.

Also make sure you install SAM CLI [https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html]
and download IntelliJ plugins for AWS toolkits for the build commands below to work

### local

Note that you need to set up your own SES first to make sure you can test this with your personal email.

Build your application with the `sam build` command.

```bash
emailer$ sam build
```

The SAM CLI installs dependencies defined in `HelloWorldFunction/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test event. An event is a JSON document that represents the input that the function receives from the event source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.

```bash
emailer$ sam local invoke HelloWorldFunction --event events/event.json
```

The SAM CLI can also emulate your application's API. Use the `sam local start-api` to run the API locally on port 3000.

```bash
emailer$ sam local start-api
emailer$ curl http://localhost:3000/
```

The SAM CLI reads the application template to determine the API's routes and the functions that they invoke. The `Events` property on each function's definition includes the route and method for each path.

```yaml
      Events:
        HelloWorld:
          Type: Api
          Properties:
            Path: /hello
            Method: get
```

### remote

To build and deploy your application for the first time, run the following in your shell:

```bash
sam build
sam deploy --guided
```