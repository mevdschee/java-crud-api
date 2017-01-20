

WARNING.. WORK IN PROGRESS.. SLOW AND BUGGY!!

###Dependencies

Install dependencies using:

    sudo apt-get install maven openjdk-7-jdk

Then build the server.

###Building a JAR file

    mvn compile assembly:single

You can execute the JAR using:

    java -jar server.jar 

You can see the api at work at http://localhost:8080/posts/1.