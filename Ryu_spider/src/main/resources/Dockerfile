FROM openjdk:8-jdk-alpine
VOLUME /tmp

RUN wget http://minio.ryuzzz.cn:30446/blog/skywalking-agent


ADD Ryu_spider-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-javaagent:/skywalking-agent/skywalking-agent.jar", "-jar", "/app.jar"]
