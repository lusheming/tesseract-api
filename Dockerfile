# 第一阶段：安装 JDK
FROM openjdk:17-jdk-slim AS jdk-stage

# 第二阶段：合并 Tesseract 和 JDK
FROM demisto/tesseract_test:1.0.0.2038079
COPY --from=jdk-stage /usr/local/openjdk-17 /usr/local/openjdk-17
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$JAVA_HOME/bin:$PATH

# 拷贝应用
COPY target/tesseract-api-0.0.1.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
#构建 docker build -t 1030918314/tesseract_test-api:0.0.3 .
#推送 docker push 1030918314/tesseract_test-api:0.0.3
