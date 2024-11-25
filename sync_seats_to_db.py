import psycopg2
import os

# Configuración de la conexión a PostgreSQL
DB_CONFIG = {
    "dbname": "cinema_db",
    "user": "cinema_user",
    "password": "cinema_pass",
    "host": "cinema-postgres",  # Usa el nombre del contenedor PostgreSQL
    "port": 5432,
}

# Obtener la ruta del archivo desde PATH_FILE
SEATS_FILE = os.getenv("PATH_FILE", "/app/data/seats.txt")

def sync_seats_to_db():
    # Verificar si el archivo existe
    if not os.path.exists(SEATS_FILE):
        print(f"Archivo {SEATS_FILE} no encontrado.")
        return

    # Leer el contenido del archivo
    with open(SEATS_FILE, "r") as file:
        lines = file.readlines()

    # Conectar a la base de datos
    conn = psycopg2.connect(**DB_CONFIG)
    cursor = conn.cursor()

    # Actualizar la tabla de reservas
    for line in lines:
        parts = line.strip().split(",")
        if len(parts) == 2:  # Formato esperado: silla,disponible
            silla = parts[0].strip()
            disponible = parts[1].strip().lower() == "true"
            cursor.execute(
                """
                INSERT INTO reservas (silla, disponible)
                VALUES (%s, %s)
                ON CONFLICT (silla) DO UPDATE SET disponible = EXCLUDED.disponible;
                """,
                (silla, disponible),
            )

    # Confirmar los cambios
    conn.commit()
    cursor.close()
    conn.close()
    print("Sincronización de sillas completada.")

if __name__ == "__main__":
    sync_seats_to_db()

