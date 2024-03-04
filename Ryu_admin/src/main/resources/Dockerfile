FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PARAMS="--spring.profiles.active=prod "
ADD Ryu_admin-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Xms1024m", "-Xmx1024m", "-jar", "/app.jar", "${PARAMS}"]
