FROM openjdk:8-jdk-alpine

# 安装curl用于下载Arthas
RUN apk --no-cache add curl

# 设置工作目录
WORKDIR /app

# 复制应用程序jar文件到工作目录
COPY Ryu_admin-0.0.1-SNAPSHOT.jar app.jar

# 下载并安装Arthas
RUN curl -O https://alibaba.github.io/arthas/arthas-boot.jar

# 暴露Arthas的8563端口
EXPOSE 8563

# 设置启动命令，包括Arthas的启动参数
ENTRYPOINT ["java", "-Xms256m", "-Xmx256m", "-jar", "app.jar", "-jar", "arthas-boot.jar"]

# 默认命令行参数，可根据需要调整
CMD ["--target-ip", "0.0.0.0"]
