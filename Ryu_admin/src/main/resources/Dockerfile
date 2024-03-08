FROM skywalking-agent
VOLUME /tmp
ENV PARAMS="--spring.profiles.active=prod "
ADD Ryu_admin-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java","-javaagent:/skywalking-agent.jar", "-jar", "/app.jar","-Dskywalking.collector.backend_service=skywalking.blog:11800","-Dskywalking.agent.service_name=ryu-admin", "${PARAMS}"]
