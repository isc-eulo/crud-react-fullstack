# 1. Usamos 17 (Obligatorio para tu pom.xml)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 2. El nombre del JAR debe ser el que genera ESTE proyecto
COPY target/clientes-react-back-0.0.1-SNAPSHOT.jar app.jar

# 3. Configuración de ejecución
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dserver.address=0.0.0.0 -Dspring.profiles.active=docker"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]