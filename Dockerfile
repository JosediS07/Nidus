# ── Etapa 1: Build del frontend ──────────────────────────────
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci --prefer-offline
COPY frontend/ ./
RUN npx ng build --configuration production

# ── Etapa 2: Build del backend ──────────────────────────────
FROM eclipse-temurin:25-jdk AS backend-build
WORKDIR /app/backend
COPY api/mvnw api/pom.xml ./
COPY api/.mvn .mvn/
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY api/src ./src
COPY --from=frontend-build /app/frontend/dist/frontend/browser/ src/main/resources/static/
RUN ./mvnw package -DskipTests -B

# ── Etapa 3: Runtime (imagen ligera) ────────────────────────
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=backend-build /app/backend/target/api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
