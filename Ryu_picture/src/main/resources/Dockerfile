FROM java:alpine
VOLUME /tmp
ADD Ryu_picture-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/app.jar"]
