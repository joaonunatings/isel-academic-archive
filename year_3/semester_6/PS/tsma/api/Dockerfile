# syntax=docker/dockerfile:1

FROM eclipse-temurin:11-jdk-jammy as base
WORKDIR /tsma
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src

FROM base as test
RUN ["./mvnw", "test"]

FROM base as development
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=development", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]

FROM base as build
RUN ./mvnw clean package -Pproduction

FROM eclipse-temurin:11-jre-jammy as production
COPY --from=build /tsma/target/tsma-*.jar /tsma.jar
