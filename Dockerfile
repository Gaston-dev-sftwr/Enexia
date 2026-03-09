# ETAPA 1: Construcción (Build) con Java 11
FROM gradle:7.6-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Compilamos el .jar de Enexia saltando los tests para ahorrar tiempo
RUN gradle build -x test --no-daemon

# ETAPA 2: Ejecución (Run) con Java 11
FROM openjdk:11-jdk-slim
EXPOSE 8081
# Copiamos el ejecutable generado en la carpeta build/libs
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]