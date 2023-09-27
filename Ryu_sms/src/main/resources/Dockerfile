FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD Ryu_sms-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/app.jar"]
