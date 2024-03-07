FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV PARAMS="--spring.profiles.active=prod "
ENV SW_AGENT_COLLECTOR_BACKEND_SERVICES=skywalking.blog:11800
ADD Ryu_admin-0.0.1-SNAPSHOT.jar app.jar
ADD ../../skywalking-agent.jar app-skywalking-agent.jar

ENTRYPOINT ["java","-javaagent:/app-skywalking-agent.jar", "-Xms1024m", "-Xmx1024m", "-jar", "/app.jar", "${PARAMS}"]
