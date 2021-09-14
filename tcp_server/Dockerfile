FROM maven:3.6.3-jdk-14 AS build
COPY ./backend/src /home/app/src
COPY ./backend/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install -Dmaven.test.skip=true -P as-dependency
COPY ./tcp_server/src /home/app/src
COPY ./tcp_server/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip=true

FROM openjdk:14-slim
COPY --from=build /home/app/target/tcp_server-0.0.1-SNAPSHOT.jar /usr/local/lib/tcp_server.jar
EXPOSE 5000
ENTRYPOINT ["java","-jar","/usr/local/lib/tcp_server.jar"]
