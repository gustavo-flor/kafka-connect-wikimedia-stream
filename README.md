# Kafka Connect Wikimedia Stream

This repository contains a sample project that can be used to start off your own source connector for Kafka Connect.

## Building the connector

The first thing you need to do to start using this connector is built it. To do that, you need to install the following dependencies:

- [Java 11+](https://openjdk.java.net/)
- [Apache Maven](https://maven.apache.org/)

After installing these dependencies, execute the following command:

```bash
mvn clean package
```

## Trying the connector

After building the connector, you can try it by using the Docker-based installation from this repository.

### 1 - Starting the environment

Start the environment with the following command:

```bash
docker-compose up -d
```

Wait until all containers are up so you can start the testing.

### 2 - Install the connector

Open a terminal to execute the following command:

```bash
curl -X POST -H "Content-Type:application/json" -d @connector.json http://localhost:8083/connectors
```

### 3 - Check the operation

Open a terminal to execute the following command:

```bash
docker exec kafka kafka-console-consumer --bootstrap-server localhost:19092 --topic another-topic
```

## We are ready to write connectors 🚀