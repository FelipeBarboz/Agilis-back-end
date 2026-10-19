# ==========================================
# Estágio 1: Build do Artefato
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copia arquivos de configuração do Maven para aproveitar cache de camadas
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x ./mvnw

# Baixa as dependências do projeto para cachear
RUN ./mvnw dependency:go-offline -B || true

# Copia o código-fonte da aplicação
COPY src ./src

# Compila e empacota o jar executável ignorando testes no container
RUN ./mvnw clean package -DskipTests -B

# ==========================================
# Estágio 2: Imagem Final de Execução (Enxuta e Segura)
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Cria usuário sem privilégios de root para segurança em produção
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copia o jar compilado do estágio de build
COPY --from=builder /build/target/*.jar app.jar

# Define permissões para o usuário não-root
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Configurações de JVM otimizadas para containers e forçando IPv4 no Render/Cloud
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Djava.net.preferIPv4Stack=true"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
