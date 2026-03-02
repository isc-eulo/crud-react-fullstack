FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiamos el JAR que mencionaste al inicio
COPY target/demo-0.0.1-SNAPSHOT-exec.jar app.jar

# Sincronizamos el puerto interno a 8081 como dice tu properties
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dserver.port=8081 -Dserver.address=0.0.0.0 -Dspring.profiles.active=docker"

# Exponemos el puerto real de la aplicación
EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]