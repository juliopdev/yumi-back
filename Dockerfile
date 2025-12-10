# --- ETAPA 1: Construcción (Build Stage) ---
# Usamos Maven con Java 21 (Eclipse Temurin) - SOPORTA --release 21
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Directorio de trabajo
WORKDIR /app

# Copia pom.xml para cache de dependencias
COPY pom.xml .

# Descarga dependencias (cacheable)
RUN mvn dependency:go-offline -B

# Copia código fuente
COPY src ./src

# Compila y empaqueta (omite tests)
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Runtime Stage) ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
# gcp-key.json /app/gcp-key.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/gcp-key.json

EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]