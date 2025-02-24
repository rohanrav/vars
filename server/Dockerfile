FROM gradle:jdk17 as builder
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon
FROM openjdk:17-slim
COPY --from=builder /home/gradle/src/build/libs/*.jar /server.jar
CMD ["java", "-jar", "/server.jar"]
