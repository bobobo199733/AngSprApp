FROM openjdk:21-jdk

WORKDIR /backend

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=/backend/build/libs/backend-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} app.jar


CMD ["java","-jar","backend-0.0.1-SNAPSHOT.jar"]