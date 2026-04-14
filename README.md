# Bibliocloud ☁️📚

Bibliocloud es una aplicación móvil nativa para Android, diseñada para la gestión, descubrimiento y consumo de recursos bibliotecarios. Permite a los usuarios acceder a un catálogo de libros, solicitar préstamos, reproducir audiolibros, escanear recursos físicos mediante la cámara y gestionar su perfil de forma conveniente.

## Características Principales

* **Autenticación (Auth):** Registro y login seguro con manejo de sesión mediante tokens JWT.
* **Catálogo:** Exploración de libros, con vista de detalle y búsqueda.
* **Préstamos (Loans):** Visualización del historial y solicitud de nuevos préstamos de material.
* **Audiolibros:** Reproductor en segundo plano usando Media3 (ExoPlayer).
* **Escaneo Modular:** Interfaz adaptada que utiliza CameraX, con miras a leer códigos de barras o QR de libros.
* **Notificaciones Push:** Integración nativa con Firebase Cloud Messaging (FCM) para recibir alertas directas del servidor (ej. cuando se aprueba un préstamo).
* **Persistencia Integrada:** Capacidad para almacenar datos localmente y consultarlos con mayor velocidad (caché) gracias a Room Database.

## Stack Tecnológico y Arquitectura

El proyecto está diseñado bajo los últimos estándares recomendados para el desarrollo Android, enfatizando el uso de **Clean Architecture** (separación por capas) y patrones como UDF (Unidirectional Data Flow):

* **Lenguaje:** [Kotlin](https://kotlinlang.org/)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) implementando componentes Material Design 3.
* **Arquitectura Base:** MVVM con separación en capas (Presentation, Domain, Data) por funcionalidad (Package by Feature).
* **Inyección de Dependencias:** [Hilt](https://dagger.dev/hilt/) para Android.
* **Redes y API:** [Retrofit 2](https://square.github.io/retrofit/) junto a OkHttp3 (implementando interceptores para autenticación con `TokenManager`).
* **Base de Datos Local:** [Room](https://developer.android.com/training/data-storage/room) con DAOs tipados.
* **Procesamiento de Audio:** Jetpack [Media3 (ExoPlayer)](https://developer.android.com/media/media3) manejado vía un `AudioService` en Foreground/Background.
* **Hardware:** Jetpack [CameraX](https://developer.android.com/training/camerax) para gestión óptima del hardware de la cámara.
* **Carga de Imágenes:** [Coil](https://coil-kt.github.io/coil/) (especialmente la integración para Compose).
* **Mensajería Push:** Firebase Cloud Messaging (FCM).

## Estructura del Código

El código de la aplicación se centraliza en `app/src/main/java/com/develazquez/bibliocloud/` bajo la siguiente estructura modular:

```text
com.develazquez.bibliocloud/
 ├── core/          # Módulos globales estables compartidos (Servicios de Audio, DI general, Notificaciones FCM, Room DB)
 ├── data/          # Implementaciones de red o capas comunes de datos.
 └── features/      # Funcionalidades de la aplicación:
      ├── audiobook # Reproducción y flujo de la UI de audiolibros
      ├── auth      # Flujos de entrada, registro y token management
      ├── camera    # Integraciones y UI de escaneo
      ├── catalog   # Consulta del acervo literario
      ├── loans     # Manejo de la lógica de los préstamos
      └── profile   # Perfilamiento del usuario logueado
```

Cada módulo en `features/` contiene comúnmente tres paquetes secundarios definiendo su división limpia:
- `data/`: Repositorios, APIs y lógica de obtención de datos.
- `domain/`: Reglas de negocio (Use Cases) y Dataclases núcleo.
- `presentation/`: Pantallas Compose y ViewModels usando StateFlows.

## Requisitos Previos

Para montar tu entorno y compilar:
- Android Studio Iguala o Ladybug.
- JDK 17.
- Emulador o Dispositivo Real usando Android 8.0 (SDK 26) o superior.

**Importante:** Para que toda la lógica de notificaciones push funcione como se espera, es requisito que añadas de manera local tu archivo `google-services.json` configurado en el directorio base `/app/`. Este proyecto usa dependencias BOM de Firebase y el plugin local.

## Instalación Rápida

1. Clona el proyecto:
```bash
git clone https://github.com/Develazquez/bibliocloud.git
```
2. Ejecuta *Sync Project with Gradle Files* en Android Studio.
3. Asegúrate de configurar la Inyección de Dependencias, Hilt requerirá que el proyecto esté compilado al menos una vez para generar los grafos adecuadamente.
4. Conecta un dispositivo físico o Inicia tu Emulador.
5. Haz clic en el botón de **Run (Play)** (`Shift + F10`).
