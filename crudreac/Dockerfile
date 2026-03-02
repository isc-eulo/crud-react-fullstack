# 1. Imagen base para Java 17 (Correcto según tu pom.xml)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 2. El nombre debe coincidir con el artifactId del pom.xml
COPY target/crudreac-0.0.1-SNAPSHOT.jar app.jar

# 3. Configuración de ejecución y puerto
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dserver.port=8081 -Dserver.address=0.0.0.0 -Dspring.profiles.active=docker"

# Puerto configurado en este proyecto
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]