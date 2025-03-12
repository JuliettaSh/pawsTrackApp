# Usar una imagen base de Java 17
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR al contenedor
COPY target/demoPT-0.0.1-SNAPSHOT.jar app.jar

# Crear la carpeta donde se guardarán las imágenes
RUN mkdir -p /app/public/imagenes

# Establecer permisos para la carpeta de imágenes
RUN chmod -R 777 /app/public/imagenes

# Exponer el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
