FROM openjdk:11 as build
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY build.gradle.kts settings.gradle.kts gradlew gradle.properties $APP_HOME/
COPY gradle $APP_HOME/gradle
RUN ./gradlew build || true
COPY . .
RUN ./gradlew build

FROM openjdk:11
ENV ARTIFACT_NAME=com.example.podrabotka-support-0.0.1-all.jar
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY --from=build $APP_HOME/build/libs/$ARTIFACT_NAME /app/podrabotka-support.jar
EXPOSE 8083
CMD ["java","-jar","podrabotka-support.jar"]