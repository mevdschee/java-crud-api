

This is a Java port of the [php-crud-api](https://github.com/mevdschee/php-crud-api) project (single file REST API). It currently only implements the core functionality.

### Dependencies

Install dependencies using:

    sudo apt-get install maven openjdk-7-jdk

Then build the server.

### Configuring

In the file "src/main/resources/jetty.properties" you can configure the listening host and port.

In "src/main/resources/hikari.properties" you can configure the MySQL connection.

### Running

To run the api (during development) type:

    mvn exec:java

In production I recommend deploying the JAR file as described below.

### Building a executable JAR file

To compile everything in a single executable JAR file, run:

    mvn compile assembly:single

You can execute the JAR using:

    java -jar server.jar

You can see the api at work at http://localhost:8080/posts/1.
