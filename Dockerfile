FROM openjdk:8
ADD build/libs/sql-mongo-client.jar /sql-mongo-client.jar
ENTRYPOINT ["java", "-jar", "sql-mongo-client.jar"]