FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mkdir -p /root/.m2 && printf '%s\n' '<settings><mirrors><mirror><id>central-mirror</id><mirrorOf>central</mirrorOf><url>https://maven.aliyun.com/repository/central</url></mirror></mirrors></settings>' > /root/.m2/settings.xml \
    && mvn -s /root/.m2/settings.xml -q -Dmaven.repo.local=/root/.m2/repository -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/policy-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
