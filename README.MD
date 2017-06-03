# Task
Design and implement a RESTful API (including data model and the backing implementation) for money 
transfers between internal users/accounts. 

## Explicit requirements:
1. Keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is invoked 
by another internal system/service)  
2. Use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the requirement #1 
3. The datastore should run in­memory for the sake of this test  
4. the final result should be executable as a standalone program (should not require a pre­installed 
container/server)  
5. Demonstrate with tests that the API works as expected  

## Architecture
1. No pre-installed server or Spring => use embedded server. For example, embedded Tomcat or Jetty. 
Spark was selected due to it is simple and I've already worked with it. Spark doesn't support 
non-blocking request which are very usefule for performance and so easy to do in Spring MVC.
2. In-memory DBs like HSQLDB or H2 doesn't support transactions, no lock read. That's why I decided not 
to use any, just store all data in plain ConcurrentHashMap.
3. We don't know what is the default currency or is there ability for different users to store money
 in different currencies. That's why it is supposed that currency is the same for everybody. 
4. Logger was added which writes all data in file `revolute-app.log` 
 
## Endpoint
`mvn clean install` command starts application and REST Endpoint for account update is available on
[http://localhost:4567/account/:id](http://localhost:4567/account/:id). For test purpose there are 4 
test accounts with ids in [1, 2, 3, 4]. Each one has balance 1000.00 units. 
 