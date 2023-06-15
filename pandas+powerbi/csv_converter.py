"""
En este archivo se procesan los datos del archivo backup.json que contiene la información exportada de la base de datos
alojada en Firebase que utiliza nuestra aplicación FitnessGym. Para poder exportar estos datos hemos generado una clave privada
de Firebase a la que hemos llamado key.json y la hemos alojado en esta carpeta. Usando el comando 
    'firestore-export --accountCredentials key.json --backupFile backup.json --prettyPrint'
de node-firestore-import-export en la consola es como generamos el archivo backup.json para poder tratarlo aquí y generar un archivo csv
para poder generar un informe en PowerBI.
"""

import json
import pandas as pd

# Leer los datos del archivo JSON
with open('backup.json') as f:
    data = json.load(f)

# Obtener la colección "clientes"
clientes = data["__collections__"]["clientes"]

# Obtener la colección "grupos"
grupos = data["__collections__"]["grupos"]

# Obtener la colección "usuarios"
usuarios = data["__collections__"]["usuarios"]

# Crear una lista para almacenar los datos de los clientes
datos_clientes = []

# Recorrer los clientes y obtener la información
for cliente_id, cliente_info in clientes.items():
    datos_cliente = {
        "cliente_id": cliente_id,
        "nombre": cliente_info.get("name", ""),
        "apellido": cliente_info.get("surname", ""),
        "fecha_nacimiento": cliente_info.get("birthdate", ""),
        "telefono": cliente_info.get("phone", ""),
        "email": cliente_info.get("email", ""),
        "grupo_id": cliente_info.get("idgroup", ""),
        "inscripcion": cliente_info.get("inscription", ""),
        "codigo_postal": cliente_info.get("postal_code", ""),
        "foto": cliente_info.get("photo", "")
    }

    # Obtener información del grupo asociado al cliente
    grupo_id = cliente_info.get("idgroup", "")
    if grupo_id in grupos:
        grupo_info = grupos[grupo_id]
        datos_cliente["nombre_grupo"] = grupo_info.get("name", "")
        datos_cliente["descripcion_grupo"] = grupo_info.get("description", "")
        datos_cliente["id_monitor"] = grupo_info.get("docMonitor", "")

        # Buscar información del usuario asociado al grupo
        usuario_id = grupo_info.get("docMonitor", "")
        usuario_info = usuarios.get(usuario_id, {})
        datos_cliente["nombre_monitor"] = usuario_info.get("first_name", "")
        datos_cliente["apellido_monitor"] = usuario_info.get("last_name", "")
        datos_cliente["birthdate_monitor"] = usuario_info.get("birthdate", "")
        datos_cliente["phone_monitor"] = usuario_info.get("phone", "")
        datos_cliente["photo_monitor"] = usuario_info.get("photo", "")
        datos_cliente["email_monitor"] = usuario_info.get("email", "")
        datos_cliente["dni_monitor"] = usuario_info.get("dni", "")
        datos_cliente["username_monitor"] = usuario_info.get("username", "")
        datos_cliente["provider_monitor"] = usuario_info.get("provider", "")
        datos_cliente["token_monitor"] = usuario_info.get("token", "")

    datos_clientes.append(datos_cliente)

# Crear un DataFrame con los datos de los clientes
clientes_df = pd.DataFrame(datos_clientes)

# Por cada columna se eliminan los espacios en blanco al principio y al final de cada cadena y luego corrige aquellos caracteres
# acentuados que se han codificado incorrectamente
for columna in clientes_df.columns:
    if clientes_df[columna].dtype == object:
        clientes_df[columna] = clientes_df[columna].apply(lambda x: x.rstrip() if isinstance(x, str) else x)
        clientes_df[columna] = clientes_df[columna].apply(lambda x: x.encode('latin-1').decode('utf-8') if isinstance(x, str) else x)

# Exportar el DataFrame a un archivo CSV
clientes_df.to_csv('fitness_gym_data.csv', index=False)

print("Archivo CSV exportado correctamente.")
