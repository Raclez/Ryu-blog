FROM openjdk:8-jdk-alpine
VOLUME /tmp

RUN wget http://ryuzzz.cn:30446/blog/apache-skywalking-java-agent-9.1.0.tgz

RUN tar -xvzf apache-skywalking-java-agent-9.1.0.tgz
ADD Ryu_web-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-javaagent:/skywalking-agent/skywalking-agent.jar", "-jar", "/app.jar","-Dskywalking.collector.backend_service=skywalking.blog:11800","-Dskywalking.agent.service_name=ryu-web"]
