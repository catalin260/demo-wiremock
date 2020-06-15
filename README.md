# Getting Started

### Prerequisites
 1. MySQL 8 (eu am instalat pe sistem versiunea =>  mysql  Ver 8.0.20-0ubuntu0.20.04.1 for Linux on x86_64 ((Ubuntu)) )
 2. Java 1.8 minim
 3. Maven
 4. Se va rula fisierul ```src/main/java/ct/wiremock/demo/dump/dump-wiremock-202006142234.sql``` pentru crearea tabelelor si importul initial al datelor
 
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
1. Aplicatia ruleaza default pe portul ```9002```.<br>
Acesta poate fi schimbat din fisierul ```src/main/resources/application.yml```, sub cheia ```server.port```

2. Datele de conectare la baza de date sunt definite tot in fisierul ```src/main/resources/application.yml```, sub cheia ```spring.datasource```
3. Am exportat colectia de request-uri din Postman. Aceasta poate fi gasita la adresa: ```src/main/resources/wiremock-localhost.postman_collection.json```
4. Cei doi useri existenti in baza de date sunt:
> user: catalin
> / pass: tudoroiu

> user: john
> / pass: tudoroiu

5.Exista un alt user cu drepturi de superadmin, care nu este definit in baza de date. Cu ajutorul acestuia se pot crea alti useri, conturi etc. Are acces la toate conturile si tranzactiile existente in baza de date etc.

Datele de conectare pentru userul master se pot gasi la sfarsitul fisierului  ```src/main/resources/application.yml```
