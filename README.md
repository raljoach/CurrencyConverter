# CurrencyConverter

Description:
-------------
CurrencyConverter is a reactive REST application that hosts a conversion API for 
transforming an amount from one currency to another.

Features:
----------
* Caching - internal caching of external Exchange API calls has been implemented

Prerequisites:
--------------
[Java 11 Installation](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) <br>
Set JAVA_HOME environment variable to path of jdk folder (i.e. C:\Program Files\Java\jdk-11.0.9)

Installation (Windows):
-----------------------
mvnw.cmd install <br>
cd target <br>
java -jar .\CurrencyConversion-0.0.1-SNAPSHOT.jar

REST API:
-----------
* from: the starting currency
* to: the destination currency
* amount: original amount to be converted
* converted: the converted amount in the destination currency

### Request

`POST /currency/convert`
    
    curl -i -H 'Accept: application/json' -X POST -d 'from=EUR&to=USD&amount=3.14' http://localhost:8080/currency/convert

### Response

    HTTP/1.1 200 OK
    Date: Thu, 24 Feb 2011 12:36:32 GMT
    Status: 200 OK
    Connection: close
    Content-Type: application/json
    Content-Length: 41

    {"from":"EUR","to":"USD","amount":"3.14","converted":3.73}



How to invoke API using Postman:
---------------------------------
![alt text](https://github.com/raljoach/CurrencyConverter/blob/master/contenttype.JPG?raw=true)
![alt text](https://github.com/raljoach/CurrencyConverter/blob/master/postman.JPG?raw=true)

Technologies Used:
--------------------
Java 11 <br>
Spring Webflux and SpringBoot 2+ <br>
Spring Reactive webclient <br>
Project Reactor (Mono)

Exchange API Dependencies:
-----------------
https://api.exchangeratesapi.io <br>
https://v6.exchangerate-api.com/

Special Gotchas:
----------------
Problems faced:
* SSL was easy to implement but not test, so SSL was omitted for now.

For future consideration:
-------------------------
* SSL/HTTPS endpoint for Currency Controller
* OAuth/JWT - Authentication to Currency Controller could be implemented using JWT
* Cache-control - browser side caching of calls to this API can be implemented in Currency Controller using cache-control header
* Versioning - versioning of requests can be implemented via headers
* Retry/Back pressure - implement retry and back off logic for external exchange API calls
* Rate limiting on Currency Controller API usage
* API keys for Currency Controller to track whose using this API
* Support for more than 3 external exchange APIs
* Support for external exchange API authentication (if required) - can be implemented using
* An option for making parallel calls to external exchange APIs, first one wins is returned
* An option to return the latest conversion rate of all external Exchange APIs, instead of just returning the first successful exchange API response

Demo:
-----
Coming soon


Questions?
-----------
Email: raljoach@gmail.com

