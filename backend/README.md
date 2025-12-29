# Spring Boot Backend

Java 17 ili Java 21  
Git  
Maven Wrapper je već uključen u projekat (`mvnw`)

## Struktura projekta

src/main/java  
Java izvorni kod

src/main/resources  
Konfiguracija, application.properties ili application.yml

src/test/java  
Testovi

pom.xml  
Maven konfiguracija projekta

## Lokalno pokretanje (development)

macOS ili Linux
```bash
./mvnw spring-boot:run


## buld packagea
./mvnw clean package -DskipTests
