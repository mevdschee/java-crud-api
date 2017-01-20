

WARNING.. WORK IN PROGRESS.. SLOW AND BUGGY!!

###Dependencies

    sudo apt-get install maven

###Building and Running the Web Application

Run with:

    mvn jetty:run

You can see the api at work at http://localhost:8080/posts/1.

###Building a JAR file

    mvn compile assembly:single

You can execute the JAR using:

    java -jar server.jar 

You can see the api at work at http://localhost:8080/posts/1.

###Building a WAR file

You can create a Web Application Archive (WAR) file from the project with the command:

    mvn package

The resulting war file is in the target directory and may be deployed on any standard servlet server or deployed to jetty. 

NB: After deploying the servlet it's path on the server will be "/java-crud-api", which comes from the artifact ID in the pom.xml file.
