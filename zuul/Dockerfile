# 测试 docker run --rm -p 7079:7079 -it suplus-discovery /bin/bash
# curl localhost:7079
#
#
FROM registry.cn-beijing.aliyuncs.com/suplus_main/openjdk:8
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
VOLUME /tmp
WORKDIR  /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Duser.timezone=GMT+8", "-jar", "app.jar"]
EXPOSE 7079

