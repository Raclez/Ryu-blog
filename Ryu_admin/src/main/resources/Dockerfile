FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV SW_AGENT_COLLECTOR_BACKEND_SERVICES=skywalking.blog:11800
ENV PARAMS="--spring.profiles.active=prod "
RUN wget -O /skywalking-agent.jar http://minio.ryuzzz.cn:30446/blog/skywalking-agent.jar

ADD Ryu_admin-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java","-javaagent:/skywalking-agent.jar", "-jar", "/app.jar", "${PARAMS}"]
