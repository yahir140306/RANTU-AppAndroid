# 📱 RANTU - Aplicación Android

Versión móvil nativa de la plataforma RANTU, construida usando **Kotlin** y **Jetpack Compose** (UI Declarativa). Diseñada con estándares de arquitectura moderna, inyección de dependencias y código modular.

## 🚀 Características Principales

- **Gestión de Propiedades:** Ver lista de cuartos disponibles, agregar nuevos y administrar cuartos propios.
- **Geolocalización In-App:** Integración con **Google Maps SDK** para visualizar propiedades en un mapa interactivo.
- **Subida de Medios:** Selector de imágenes, subida a Supabase Storage y visualización en carruseles fluidos (`Coil`).
- **Comentarios y Ratings:** Sistema de reseñas interactivo con actualización en tiempo real.
- **Tarjetas Descargables:** Los usuarios pueden descargar un *Flyer* del cuarto directamente a la galería del teléfono.
- **Soporte para Deep Links:** Al hacer clic en un enlace de RANTU, la aplicación intercepta y abre el cuarto específico.

---

## 🏗️ Arquitectura y Clean Code

La aplicación dejó atrás las malas prácticas y adoptó un esquema estructurado para facilitar la escalabilidad y las pruebas:

1. **Jetpack Compose (One Component Per File):**
   - Eliminación del patrón "Archivo Dios" (Megabloques). Todos los componentes de UI (`TopBar`, `RoomCard`, `FilterBar`, `StarRating`, `CommentCard`) están estrictamente aislados en sus propios archivos dentro del paquete `Components/`.
   - Reutilización masiva: Pantallas como `AddRoomScreen` y `EditRoomScreen` consumen piezas compartidas (`RoomFormDetails`, `RoomImageUploader`), eliminando 100% el código duplicado.

2. **Inyección de Dependencias Manual (DI):**
   - Archivo `di/AppModule.kt` y `di/ViewModelFactory.kt` encargados de instanciar e inyectar el `RoomRepository` a los ViewModels. Ningún ViewModel construye sus propias dependencias, lo cual los hace 100% testeables.

3. **Validación Centralizada:**
   - Objeto `RoomFormValidator` en `utils/` que unifica las reglas de negocio para asegurar que la adición o edición de cuartos sigan los mismos criterios.

4. **Strings Resources (i18n):**
   - Sustracción de textos fijos ("Hardcoded") hacia `res/values/strings.xml`, preparando la app para fácil internacionalización.

---

## 🧪 Pruebas (Testing)

Se implementó **JUnit4** y **MockK** para pruebas unitarias.

### ¿Cómo ejecutar los Tests?
Las pruebas unitarias validan el comportamiento lógico sin necesidad de arrancar un emulador pesado:

1. Abrir el proyecto en **Android Studio**.
2. En la ventana "Project", navegar a `app/src/test/java/...`.
3. Clic derecho sobre una clase de test (ej. `RoomDetailViewModelTest` o `RoomFormValidatorTest`) y seleccionar **Run 'TestName'**.

**Casos cubiertos actualmente:**
- **Inyección de Dependencias / ViewModels:** Simulación (Mock) del Repositorio para probar que el estado (`UiState`) se actualice correctamente cuando hay éxito o cuando ocurre un error de red (`Exception`).
- **Validaciones:** Se comprueba que los campos incompletos devuelvan el mensaje de error del recurso correspondiente.

---

## 🛠️ Instalación y Configuración

1. Clonar el repositorio.
2. Abrir en **Android Studio**.
3. Asegurarse de tener una API Key de Google Maps en el archivo `local.properties` (ej. `MAPS_API_KEY=AIzaSy...`).
4. Configurar URL y Key de Supabase en el código cliente.
5. Ejecutar (Sincronizar Gradle y compilar).

## 📜 Licencia y Autor
Desarrollado por el equipo de RANTU.
