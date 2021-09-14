FROM maven:3.6.3-jdk-14 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true -P normal

FROM openjdk:14-slim
COPY --from=build /home/app/target/backend-0.0.1-SNAPSHOT.jar /usr/local/lib/backend.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/backend.jar"]
