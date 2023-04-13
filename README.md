# FitnessGym

![](http://drive.google.com/uc?export=view&id=1U_3kmoIH0hH12VUwncm5gtksl0WUlnuu)

Desarrollado por Jose Antonio Benitez (Hibrido) y Alberto Moreno (Nativo)

## Objetivo del Proyecto
Crear una aplicacion que en el caso Hibrido gestionara los clientes, grupos , monitores y maquinas de un gimnasio y en el caso Nativo gestionara el perfil del cliente, los grupos a los que el cliente se ha apuntado, los monitores y entrenadores personales que le han sido asignados y gestionar su subcripcion al gimnasio.

## Base de datos
La base de datos sera el firebase y se usara en ambas aplicaciones. Un cliente no puede borrar monitores, ni grupos pero si puede quitarse de un grupo. En cambio el administrador del gimnasio si puede borrar los grupos, subcripciones, monitores y maquinas.

## Modelo Relacional
![](http://drive.google.com/uc?export=view&id=1eG7NFQKbKK497GwGSbLkR8WeHlwjl3FI)

La aplicación consta de 5 modelos. El modelo usuarios refiere a los clientes del gimnasio (usuarios_gym). De estos se necesita su nombre, apellidos, email, fecha de nacimiento, código postal, número de teléfono, DNI/NIE, una foto de perfil y la duración de su suscripción. Luego tenemos la entidad grupos, que refiere a los clases y actividades que se imparten en el gimnasio. Necesitan nombre, descripción y foto. Un usuario puede estar en muchos grupos y un grupo tiene muchos usuarios. Al cargo de cada grupo esta un monitor, que es un empleado del gimnasio (empleado_gym). De estos se necesita su nombre, apellidos, nombre de usuario, fecha de nacimiento, email, contraseña, numero de telefono, DNI/NIE y foto de perfil. Un empleado puede tener muchos grupos a su cargo pero un grupo solo puede un empleado al frente. 

## Trello
Este proyecto utiliza Trello para la gestión de las tareas a realizar
https://trello.com/b/bwXyty7u/fitnessgym


## Anteproyecto 
https://www.figma.com/file/kvU6qBh4NmjaGoooBiBPvJ/Anteproyecto-Fitness-Gym?node-id=0%3A1&t=e7FTqe0I8Yq6Mbhf-1
