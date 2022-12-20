FROM gradle:7.4-jdk17-alpine as builder
WORKDIR /build
COPY . /build/
RUN chmod 777 gradlew
RUN ./gradlew bootJar
WORKDIR /build/build/libs

FROM eclipse-temurin:11-jre-alpine
COPY --from=builder /build/build/libs/kakaoasset-0.0.1-SNAPSHOT.jar kakaoasset-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "kakaoasset-0.0.1-SNAPSHOT.jar"]
