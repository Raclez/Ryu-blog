FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PARAMS="--spring.profiles.active=prod --spring.cloud.nacos.config.server-addr=naocs-6fie.blog:8848 --spring.cloud.nacos.discovery.server-addr=naocs-6fie.blog:8848"

ADD Ryu_search-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/app.jar", "${PARAMS}"]
