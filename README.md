# CurrencyConverter

Description:
-------------
CurrencyConverter is a reactive REST application that hosts a conversion API for 
transforming an amount from one currency to another.

Prerequisites:
--------------
Java 11 https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
Set JAVA_HOME environment variable to path of jdk folder (i.e. C:\Program Files\Java\jdk-11.0.9)

Installation (Windows):
-----------------------
mvnw.cmd install
cd target
java -jar .\CurrencyConversion-0.0.1-SNAPSHOT.jar

How to invoke API using Postman:
---------------------------------
![alt text](https://github.com/raljoach/CurrencyConverter/blob/master/Postman.jpg?raw=true)

Technologies Used:
--------------------
Java 11
Spring Webflux/Project Reactor and SpringBoot 2+
Spring Reactive webclient

Special Gotchas:
----------------
Problems faced:
* SSL was easy to setup but couldn't get integration tests to successful send calls to REST endpoint without getting SSL errors

Demo:
-----
Coming soon


Questions?
-----------
Email: raljoach@gmail.com

