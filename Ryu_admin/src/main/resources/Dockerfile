FROM openjdk:8-jdk-alpine
VOLUME /tmp


RUN wget http://minio.ryuzzz.cn:30446/blog/skywalking-agent



ADD Ryu_admin-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java","-javaagent:/skywalking-agent/skywalking-agent.jar", "-jar", "/app.jar","-Dskywalking.collector.backend_service=skywalking.blog:11800","-Dskywalking.agent.service_name=ryu-admin","-Dskywalking.plugin.toolkit.log.grpc.reporter.server_host=skywalking.blog ","-Xms512m","-Xmx512m"]
