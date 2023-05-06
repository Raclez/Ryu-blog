
**下载地址：**

```
https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec
```

**运行：**

```
java -jar zipkin-server-2.12.5-exec.jar

# 或集成RabbitMQ

java -jar zipkin-server-2.12.5-exec.jar --zipkin.collector.rabbitmq.addresses=127.0.0.1
```