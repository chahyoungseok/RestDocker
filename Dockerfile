FROM openjdk:17-jdk-slim as build

WORKDIR /workspace/app
COPY . /workspace/app

RUN ./gradlew clean bootjar && chmod 755 /workspace/app/build/libs/*.jar

FROM azul/zulu-openjdk-alpine:17-jre-latest

COPY --from=build /workspace/app/build/libs/*.jar /home/restdocker.jar

ENTRYPOINT ["java", "-jar", "/home/restdocker.jar"]
