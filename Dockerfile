FROM node:16-alpine as base
#FROM openjdk:13-jdk-alpine as build

RUN apk --update --no-cache upgrade \
    && apk add java-cacerts openjdk13 \
    && apk add nodejs npm  \
    && node --version \
    && npm --version

FROM base as build
WORKDIR /workspace/app
COPY . .
#COPY mvnw .
#COPY .mvn .mvn
#COPY pom.xml .
#COPY frontend frontend
#COPY src src
COPY application.properties.docker src/main/resources/application.properties

RUN chmod o+x mvnw && ./mvnw install -DskipTests --batch-mode -Pproduction
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM openjdk:13-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency

RUN apk --update --no-cache upgrade \
    && apk add git openssh-client

RUN mkdir -p /git && mkdir -p /uploads && mkdir -m 0700 /root/.ssh 
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","com.emmenemoi.application.Application"]