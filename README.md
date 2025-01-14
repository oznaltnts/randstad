# randstad

case study for randstad

This is a Java / Maven / Spring Boot (version 3.4.1) / H2DB application.

## How to Run

You run it using the ```java -jar``` command.

* Clone this repository
* You can build the project by running ```mvn clean package```

```
    java -jar target/randstad-1-SNAPSHOT.war
```

Once the application runs, create some customer records via h2 console at http://localhost:9000/h2-console

INSERT INTO customers(id,name,surname,credit_limit,used_credit_limit) VALUES (1,'ozan','altintas', 100, 0);
INSERT INTO customers(id,name,surname,credit_limit,used_credit_limit) VALUES (2,'dilan','agirbas', 200, 0);






