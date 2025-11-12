FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY src src

RUN gradle clean build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8000

ENV SPRING_PROFILES_ACTIVE=dev \
    CONFIG_SERVER_URI=http://config-server:8888 \
    EUREKA_URI=http://eureka-server:8761/eureka/ \
    RABBITMQ_HOST=rabbitmq \
    RABBITMQ_PORT=5672 \
    RABBITMQ_USER=guest \
    RABBITMQ_PASS=guest \
    MAIL_USER=arkanotification@gmail.com \
    MAIL_PASSWORD=akbv_rpvx_ygzs_ylit

ENTRYPOINT ["java", "-jar", "app.jar"]

