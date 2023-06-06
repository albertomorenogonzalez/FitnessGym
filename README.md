# FitnessGym

![](http://drive.google.com/uc?export=view&id=1U_3kmoIH0hH12VUwncm5gtksl0WUlnuu)

Desarrollado por Jose Antonio Benitez (Hibrido) y Alberto Moreno (Nativo)

## Objetivo del Proyecto
El objetivo del proyecto es crear una aplicación para un gimnasio que agilice y facilite la gestión del mismo.

## Base de datos
La base de datos utilizada por ambas aplicaciones será Firebase.

## Modelo Relacional
![](http://drive.google.com/uc?export=view&id=1eG7NFQKbKK497GwGSbLkR8WeHlwjl3FI)

La aplicación consta de 3 modelos. El modelo usuarios refiere a los clientes del gimnasio (usuarios_gym). De estos se necesita su nombre, apellidos, email, fecha de nacimiento, código postal, número de teléfono, DNI/NIE, una foto de perfil y la duración de su suscripción. Luego tenemos la entidad grupos, que refiere a los clases y actividades que se imparten en el gimnasio. Necesitan nombre, descripción y foto. Un usuario puede estar en muchos grupos y un grupo tiene muchos usuarios. Al cargo de cada grupo esta un monitor, que es un empleado del gimnasio (empleado_gym). De estos se necesita su nombre, apellidos, nombre de usuario, fecha de nacimiento, email, contraseña, numero de telefono, DNI/NIE y foto de perfil. Un empleado puede tener muchos grupos a su cargo pero un grupo solo puede un empleado al frente. 

## Requisitos Específicos del Módulo Sistemas de Gestión Empresiaral (SGE)
Para esta asignatura procederemos a descargar los datos de los clientes (CSV o JSON), manipularlos con Pandas y crear un nuevo fichero que le sirva de entrada a PowerBI.

## Requisitos Específicos del Módulo Desarrollo de Interfaces
Para la asignatura de diseño de interfaces crearemos un informe a partir de esos datos en el que mostraremos gráficas como rangos de edad, grupos más/menos solicitados, monitores que están a cargo de más/menos grupos. Se subirá al repositorio del proyecto y también se publicará en Power BI, compartiéndolo con la dirección de correo educativa del profesor.

## [Trello](https://trello.com/b/bwXyty7u/fitnessgym)
Este proyecto utiliza Trello para la gestión de las tareas a realizar

## [Anteproyecto en Figma](https://www.figma.com/file/kvU6qBh4NmjaGoooBiBPvJ/Anteproyecto-Fitness-Gym?node-id=0%3A1&t=e7FTqe0I8Yq6Mbhf-1)

## [Vídeo Checkout 05/05/2023](https://www.youtube.com/watch?v=go-7G-VvBFE)

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
