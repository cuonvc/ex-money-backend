FROM eclipse-temurin:17

COPY ./target/exmoney-0.0.1-SNAPSHOT.jar exmoney.jar
ENTRYPOINT ["java","-Dspring.profiles.active=staging","-jar","/exmoney.jar"]