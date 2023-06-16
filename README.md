# FitnessGym

<p align="center">
 <img src="http://drive.google.com/uc?export=view&id=1z4ASt_DywpE8ZdOw1pL5ddVmi1d9Syxh">
</p>
 
Desarrollado por Jose Antonio Benitez (Hibrido) y Alberto Moreno (Nativo)

## Objetivo del Proyecto
El objetivo del proyecto es crear una aplicación para un gimnasio que agilice y facilite la gestión del mismo.

## [APK](https://github.com/albertomorenogonzalez/FitnessGym/blob/main/app-debug.apk)

## Base de datos
La base de datos será en Firebase y se utilizará en ambas aplicaciones. Un empleado puede estar en muchos grupos, pero un grupo solo puede tener un empleado. Un grupo puede tener muchos clientes y un cliente puede estar en un grupo.

La aplicación consta de 3 modelos:

### Modelo "clientes"
- Nombre
- Apellidos
- Email
- Fecha de nacimiento
- Código postal
- Número de teléfono
- DNI/NIE
- Foto de perfil
- Inscripción

### Modelo "grupos"
- Nombre
- Descripción
- Foto

### Modelo "empleado_gym" (usuario y monitor)
- Nombre
- Apellidos
- Fecha de nacimiento
- Email
- Contraseña
- Número de teléfono
- DNI/NIE
- Foto de perfil

Al cargo de cada grupo está un monitor, que es un empleado del gimnasio. Un empleado puede tener muchos grupos a su cargo, pero un grupo solo puede tener un empleado al frente.

## Modelo Relacional
![](http://drive.google.com/uc?export=view&id=1eh7wx4V_9hpIglzhcW6GDkvTwc0yYqxg) 

## Requisitos Específicos del Módulo Sistemas de Gestión Empresarial (SGE)
Para esta asignatura procederemos a descargar los datos de los clientes (CSV o JSON), manipularlos con Pandas y crear un nuevo fichero que le sirva de entrada a PowerBI.

Para poder crear un archivo que realizase esta tarea, hemos necesitadoprocesar los datos del archivo backup.json que contiene la información exportada de la base de datos
alojada en Firebase que utiliza nuestra aplicación FitnessGym. Para poder exportar estos datos hemos generado una clave privada
de Firebase a la que hemos llamado key.json y la hemos alojado en esta carpeta. Usando el comando 
    'firestore-export --accountCredentials key.json --backupFile backup.json --prettyPrint'
de node-firestore-import-export en la consola es como generamos el archivo backup.json para poder tratarlo aquí y generar un archivo csv
para poder generar un informe en PowerBI.

[Ver el archivo](pandas+powerbi/csv_converter.py)

## Requisitos Específicos del Módulo Desarrollo de Interfaces
Para la asignatura de diseño de interfaces crearemos un informe a partir de esos datos en el que mostraremos gráficas como rangos de edad, grupos más/menos solicitados, monitores que están a cargo de más/menos grupos. Se subirá al repositorio del proyecto y también se publicará en Power BI, compartiéndolo con la dirección de correo educativa del profesor.

![](https://github.com/jbenrui/fitness-gym/blob/master/Imagenes/PBI.png)

## Manual de Instalación y Dependencias
### 1. Introducción
En este apartado del README se explicará todo aquello necesario para instalar la aplicación y que su uso sea óptimo. 
### 2. Requisitos Previos
Esta aplicación ha sido creada en Android Studio 2021.3.1, usando la versión 1.8 de Java, en el sistema operativo Windows y enfocada a la API 33 de Android. La versión mínima que soporta es la API 26. La aplicación está escrita en Kotlin y utiliza XML para las vistas. Para la base de datos se utiliza Firebase como ya se ha mencionado anteriormente.
### 3. Instalación
Si quieres únicamente usar la aplicación será mediante la descarga del [APK](https://github.com/albertomorenogonzalez/FitnessGym/blob/main/app-debug.apk) 
Si quieres colaborar y trabajar sobre ella solo tienes que clonar tu repositorio con git clone y asegurarte de que tienes los requisitos comentados previamente. Recomendación personal para probar la aplicación usar tu dispositivo físico. Para ello solo necesitas conectar tu teléfono en modo desarrollador y con la opción de depuración USB activadad en el ordenador donde estés programando 
### 4. Dependencias Utilizadas
implementation 'androidx.core:core-ktx:1.7.0'
- androidx.appcompat:appcompat:1.6.1
- com.google.android.material:material:1.8.0
- androidx.constraintlayout:constraintlayout:2.1.4
- junit:junit:4.13.2
- androidx.test.ext:junit:1.1.5
- androidx.test.espresso:espresso-core:3.5.1
- platform('com.google.firebase:firebase-bom:31.3.0')
- com.google.firebase:firebase-auth-ktx:22.0.0
- com.google.firebase:firebase-firestore-ktx:24.6.1
- com.google.firebase:firebase-analytics-ktx
- com.google.firebase:firebase-storage-ktx:20.2.0
- de.hdodenhof:circleimageview:3.1.0
- com.github.bumptech.glide:glide:4.15.0
- com.github.bumptech.glide:compiler:4.15.0
### 5. Otras Recomendaciones Personales
- Asignar en  buildFeatures viewBindign a true en el archivo build.gradle (app)
- Preguntar cualquier duda a través de este repositorio o a través de mi correo albertomorenogonzalez03@gmail.com

## Tutorial de Uso de la Aplicación

Este es icono de la aplicación en el teléfono.

<p align="center">
 <img src="http://drive.google.com/uc?export=view&id=1NkJtW-BiciO2XjVbceO4QqXtBpn53-Y6" width="40%">
</p>

Al clickar nos aparecerá lo siguiente:

<p align="center">
 <img src="http://drive.google.com/uc?export=view&id=15XoHJChh6kBcsG9RDVwHjNpEnCjo1Lpi" width="40%">
</p>

Démosle primero al botón de Registrarse. Al completar el registro en el que el único campo no obligatorio es la foto, volveremos a la actividad del login pero con nuestros datos en los campos de entrada. Solo tendremos que darle a "Iniciar"

<p align="center">
 <img src="http://drive.google.com/uc?export=view&id=1PBpMaM95_4h2d7mv-X9-xi4PtexIIFDS" width="40%">
 <img src="http://drive.google.com/uc?export=view&id=1lK8EQCuoqCMlhZHww1iDLVkSTXUhCQG3" width="40%">
</p>

Nos aparece la actividad principal, desde donde podremos navegar a las distintas partes de la aplicación. En este momento podemos desplegar el side menu o hacer una pulsación larga para ver más opciones:

<p align="center">
 <img src="http://drive.google.com/uc?export=view&id=1eGp-Aubq0tVxLcQ8lsN_4-eW5Sxs2FAv" width="30%">
 <img src="http://drive.google.com/uc?export=view&id=1DrJWciYu6av8PHYRqk4Jnx8TcC1rOYX5" width="30%">
 <img src="http://drive.google.com/uc?export=view&id=1xYpNQIaG-Iz5zQM5IpqZXEUZZfOIfQbX" width="30%">
</p>

En la pestaña de clientes podremos añadir un nuevo cliente, editarlo, ver sus datos, eliminarlo, asignarlo a un grupo, cambiarlo de grupo o quitarlo de un grupo.



En la pestaña de grupos podremos añadir un nuevo grupo, ver todos sus datos, editarlo, eliminar y ver los clientes pertenecientes a ese grupo.



En la pestaña de instructores podremos ver los instructores, monitores y empleados y ver sus datos.



En la pestaña de perfil, a la que podremos acceder tanto desde su entrada en el side menu como clickando en nuestra foto de perfil, y ahi podremos ver nuestros datos, editarlos, eliminar nuestro usuario e ir a opciones.



En la pestaña de opciones podremos cambiar el idioma de nuestra aplicación. Al confirmar, apareceremos de nuevo en la pestaña de inicio con el idioma de la aplicación completa cambiado.



## [Trello](https://trello.com/b/bwXyty7u/fitnessgym)
Este proyecto utiliza Trello para la gestión de las tareas a realizar 

## [Anteproyecto en Figma](https://www.figma.com/file/kvU6qBh4NmjaGoooBiBPvJ/Anteproyecto-Fitness-Gym?node-id=0%3A1&t=e7FTqe0I8Yq6Mbhf-1)

## [Vídeo Checkout 05/05/2023](https://www.youtube.com/watch?v=go-7G-VvBFE)

## [Vídeo de la Aplicación final - 16/06/2023]()

## Histórico del Proyecto (por commits principales)

 - [First Commit of the Project. Functional log in added - 13/04/2023](https://github.com/albertomorenogonzalez/FitnessGym/commit/78507842a6f12c3de6e2473f1c60cdfd34f60752)
 
 Añadido el login, maquetado y funcional. Añadidos también archivos multimedia. Conexión con firebase.
 
 
 - [Functional Register function (only email and password) - 04/05/2023](https://github.com/albertomorenogonzalez/FitnessGym/commit/73a034016e88b3bc3bf0049c7352fe404ceea6bf)
 
 Añadido register maquetado y funcional, aunque solo se utilizan para el registro en firebase el email y la contraseña. Añadidos archivos de recursos multimedia y correciones menores.
 
 
 - [Navigation added. Side Menu and Fragment Views - 07/05/2023](https://github.com/albertomorenogonzalez/FitnessGym/commit/5cdb7d0a8dac8e8595a36adf9c47b6c910ca6474)
    
 Añadida la navegación de la aplicación una vez realizado el log in. Esta navegación es posible gracias a un side menu.
 
 
 - [Improved App Navigation and Registration of Users - 06/06/2023](https://github.com/albertomorenogonzalez/FitnessGym/commit/5db6430e00f3fde38793fa66558518053a462897)

Ahora la navegación dentro de la aplicación es más fluida, el título en la action bar cambia correctamente tanto si vas hacia otro fragmento como si vuelves después de haber estado en otro. Tanto en la Login Activity como en la MainActivity en el caso de que se encuentre en el fragmento inicial al salir de la aplicación pulsando el back button aparecerá un diálogo que te preguntará si quieres seguir o no en la aplicación.
Ahora al registrar un usuario todos los campos necesarios para que pueda ser registrado son guardados en una colección de firebase. Una vez realizado el registro se volverá a la LoginActivity con el email y la contraseña del usuario recién registrado en los campos de entrada para que este solo tenga que darle a iniciar sesión. En la cabecera del menú lateral se mostrará su foto de perfil si tiene y su nombre. 
Problemas: Al subir una foto proveniente directamente de la galería ocurre un error aún por solucionar. Con las fotos hechas con la camara esto no sucede


- [Profile Functionality Completed - 09/06/2023](https://github.com/albertomorenogonzalez/FitnessGym/commit/73e9001bcd111568fbf6e8b45cd7b0c6e653b4b3)

Al entrar en el ProfileFragment se pueden ver todos los datos del usuario activo, y se puede tanto eliminar el usuario como editar los datos. 
El problema con las imágenes de galería en la RegisterActivity aún no está solucionado, pero en EditProfileActivity no hay ningún problema.


- [16/06/2023](https://github.com/albertomorenogonzalez/FitnessGym/commit/e7d51a451573292125812f7518ff2774ff165070)

Aplicación compleatada. Todas las vistas se muestran, navegación entre las distintas partes de la aplicación óptima. CRUDs completados. Cambio de lenguage de la aplicación funcional. No ha sido posible implementar el cambio de tema y por eso se muestra un texto que indica que aún no se puede

Aplicación comentada (en inglés) y APK subida. Arreglados todos los problemas excepto el de la galería en el RegisterActivity
