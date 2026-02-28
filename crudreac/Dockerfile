# 1. Imagen base obligatoria para Java 17
FROM eclipse-temurin:17-jre-alpine

# 2. Directorio de trabajo
WORKDIR /app

# 3. Copiar el JAR (Nombre exacto según tu pom.xml)
COPY target/clientes-react-back-0.0.1-SNAPSHOT.jar app.jar

# 4. Variables de entorno
# Mantenemos el perfil docker y habilitamos el puerto 8080
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dserver.address=0.0.0.0 -Dspring.profiles.active=docker"

# 5. Puerto estándar de este proyecto
EXPOSE 8080

# 6. Ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]