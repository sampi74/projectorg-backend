# ProjectOrg - Backend

Este es el backend de **ProjectOrg**, una aplicación de gestión de proyectos. Está desarrollado con **Spring Boot** y utiliza **PostgreSQL** como base de datos.

## 🚀 Tecnologías
- **Java 17**
- **Spring Boot**
- **Spring Security (JWT)**
- **PostgreSQL**
- **Gradle**
- **Hibernate**

## 📂 Estructura del Proyecto
```
projectorg-backend/
│── src/main/java/com/projectorg/
│   ├── controllers/      # Controladores REST
│   ├── entities/         # Entidades JPA
│   ├── repositories/     # Repositorios JPA
│   ├── services/         # Servicios de negocio
│   ├── security/         # Configuración de autenticación JWT
│── src/main/resources/
│   ├── application.properties  # Configuración de la base de datos
│── build.gradle                # Configuración de Gradle
│── README.md                   # Documentación
```

## ⚙️ Configuración

1. **Clonar el repositorio:**
```sh
git clone https://github.com/tuusuario/projectorg-backend.git
cd projectorg-backend
```

2. **Configurar PostgreSQL:**
    - Crear una base de datos llamada `projectorg`
    - Configurar `application.properties` con tus credenciales:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/projectorg
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

3. **Compilar y ejecutar el backend:**
```sh
./gradlew bootRun
```

El backend estará disponible en `http://localhost:8080/`.

## 🔑 Autenticación
El backend utiliza **JWT** para la autenticación. Para acceder a los endpoints protegidos:
1. Hacer login en `/auth/login` con un usuario registrado.
2. Incluir el token en las solicitudes con el header `Authorization: Bearer <TOKEN>`.

## 📌 Endpoints Principales
- `POST /auth/login` → Autenticación de usuarios.
- `GET /users` → Listar usuarios (requiere autenticación).
- `GET /projectorg/projects` → Listar proyectos.
- `PUT /projectorg/projects/{projectId}/tasks/{taskId}` → Editar tarea.

## 📜 Licencia
Este proyecto está bajo la licencia **MIT**.

