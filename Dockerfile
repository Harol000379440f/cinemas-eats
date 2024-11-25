# Usar una imagen base de OpenJDK con Java 17
FROM eclipse-temurin:17-jdk-jammy

# Configurar una variable de entorno para seleccionar el ambiente (por defecto: DEV)
ARG ENV=DEV
ENV APP_ENV=${ENV}

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar todos los archivos del proyecto al contenedor
COPY . .

# Construir el archivo .jar usando Gradle
RUN ./gradlew clean bootJar --no-daemon

# Copiar el archivo de configuración único al contenedor
COPY src/main/resources/application.properties /app/config/application.properties

# Copiar el archivo seats.txt al contenedor
COPY src/main/resources/seats.txt /app/data/seats.txt

# Configurar la variable de entorno PATH_FILE
ENV PATH_FILE=/app/data/seats.txt

# Definir la convención de nombres para los archivos .jar según el ambiente
ENV JAR_DEV=cinemaseats-0.0.1-SNAPSHOT.jar
ENV JAR_QA=cinemaseats-0.0.1-PRERELEASE.jar
ENV JAR_PROD=cinemaseats-0.0.1.jar

# Copiar el script Python de sincronización
COPY sync_seats_to_db.py /app/scripts/sync_seats_to_db.py

# Instalar Python, cron y dependencias
RUN apt-get update && apt-get install -y \
    cron \
    python3 \
    python3-pip && \
    pip3 install psycopg2-binary

# Crear cron job para sincronización cada minuto
RUN echo "* * * * * python3 /app/scripts/sync_seats_to_db.py >> /var/log/cron_seats.log 2>&1" > /etc/cron.d/cron_sync && \
    chmod 0644 /etc/cron.d/cron_sync && \
    crontab /etc/cron.d/cron_sync

# Exponer el puerto configurado en Spring Boot
EXPOSE 8081

# Usar un script de entrada para seleccionar el archivo .jar correcto según el ambiente y reiniciar cron
COPY entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Comando de inicio del contenedor
CMD ["sh", "-c", "service cron start && /app/entrypoint.sh"]

