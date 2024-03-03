FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PARAMS="--spring.profiles.active=prod --spring.cloud.nacos.config.server-addr=naocs-6fie.blog:8848 --spring.cloud.nacos.discovery.server-addr=naocs-6fie.blog:8848"
ADD Ryu_admin-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Xms1024m", "-Xmx1024m", "-jar", "/app.jar", "${PARAMS}"]
