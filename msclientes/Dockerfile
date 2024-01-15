FROM maven:3.8.5-openjdk-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM openjdk:17-oracle
WORKDIR /app
COPY --from=build ./app/target/*.jar ./app.jar

ENV EUREKA_SERVER=ms-eurekaserver
ENTRYPOINT java -jar app.jar