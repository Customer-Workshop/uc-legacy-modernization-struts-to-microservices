FROM maven:3.9.9-eclipse-temurin-21 AS build
ARG MAVEN_MIRROR=https://repo.maven.apache.org/maven2
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mkdir -p /root/.m2 && printf '<settings><mirrors><mirror><id>central-mirror</id><mirrorOf>central</mirrorOf><url>%s</url></mirror></mirrors></settings>\n' "$MAVEN_MIRROR" > /root/.m2/settings.xml \
    && mvn -s /root/.m2/settings.xml -q -Dmaven.repo.local=/root/.m2/repository -DskipTests package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/claims-intake-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]
