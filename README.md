# Getting Started

### Prerequisites
 1. MySQL 8 (the version installed on my system =>  mysql  Ver 8.0.20-0ubuntu0.20.04.1 for Linux on x86_64 ((Ubuntu)) )
 2. Java 1.8 minimum
 3. Maven
 4. Run the database migration ```src/main/java/ct/wiremock/demo/dump/dump-wiremock-202006142234.sql```
 
 ### Build
 ```
$ cd <project_dir>
$ mvn clean install
```
 
### Run
 ```
$ cd <project_dir>/target
$ java -jar demo-0.0.1-SNAPSHOT.jar
```

### Good to know
1. The default port on which application is running is: ```9002```.<br>
This can be changed in the file ```src/main/resources/application.yml```, modifying the key ```server.port```

2. Database connection info: ```src/main/resources/application.yml```, under ```spring.datasource```
3. Postman requests collection: ```src/main/resources/wiremock-localhost.postman_collection.json```
4. Existing users in the database:
> user: catalin
> / pass: tudoroiu

> user: john
> / pass: tudoroiu

5. There is another user with superadmin rights, which is not defined in the database. When you authenticate with this one, you can create other users, accounts etc. It has access to all accounts and transactions existing in the database etc.

You can find the credentials for this user at the end of the file ```src/main/resources/application.yml```
