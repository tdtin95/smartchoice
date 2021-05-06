FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 9083
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=production"]