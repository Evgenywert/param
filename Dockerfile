# =========================================
# 🏗 Stage 1 — Сборка приложения (builder)
# =========================================
FROM --platform=linux/amd64 maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# копируем pom.xml и загружаем зависимости (кэшируется)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# копируем исходники и собираем jar
COPY src ./src
RUN mvn clean package -DskipTests


# =========================================
# 🚀 Stage 2 — Запуск приложения (runtime)
# =========================================
FROM --platform=linux/amd64 eclipse-temurin:17-jre-jammy
WORKDIR /app

# копируем готовый jar из builder
COPY --from=builder /app/target/*.jar app.jar

# открываем порт приложения
EXPOSE 8080

# активный Spring профиль
ENV SPRING_PROFILES_ACTIVE=prod

# команда запуска
ENTRYPOINT ["java", "-jar", "app.jar"]