# Advertiser Service
Simple REST API service to manage advertisers

Author
-
[Alexander Drozdov](mailto:aleksandr.drozdov.99@gmail.com)

Languages
-
Scala

Dependencies
-

- Scala 2.12.7
- Java 8
- Docker 1.17.x
- Docker-compose 1.22.x

In this application using default H2 db configuration.
Also using im-memory settigns, and every rerun of application will nullify data.
You can change in src/main/resources/application.conf

Running
-

### Docker run 

- clones repository
- creates a Docker image
- launch the application

```bash
$ git clone https://github.com/IAmDrozdov/adv-service.git
$ cd adv-service
$ docker build -t adv-service .
$ docker run --network host adv-service
```

### Run without docker

After cloning
```bash
sbt run
```

#### Testing

```bash
$ sbt clean coverage test
```

To generate the coverage reports run

```bash
$ sbt coverageReport
```
Coverage reports will be in  ${projectDir}/target/scala-2.12/scoverage-report
