# === STAGE 1: Build ===
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro — isso ajuda o Docker a usar cache de dependências
COPY pom.xml .

# Baixa dependências para cache (sem compilar ainda)
RUN mvn dependency:go-offline

# Agora copia o restante do código
COPY src ./src

# Compila o projeto e gera o .jar (sem testes)
RUN mvn clean package -DskipTests

# === STAGE 2: Runtime ===
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copia o jar gerado da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta usada pelo Spring Boot
EXPOSE 8080

# Adiciona usuário não root por segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

# Define timezone (opcional, útil para logs)
ENV TZ=America/Sao_Paulo

# Comando de execução da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
