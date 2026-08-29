# Guía de Testing para RANTU Android 🧪

Esta guía detalla cómo está configurado el entorno de pruebas en la aplicación Android de RANTU. Utilizar pruebas asegura que cuando agregamos pantallas nuevas o modificamos componentes de Jetpack Compose, no rompemos lo que ya funciona.

## Frameworks de Testing Utilizados

1. **JUnit 4 / JUnit 5**: Para pruebas unitarias (Unit Tests). Se usa para probar lógica pura de Kotlin sin necesidad de un dispositivo o emulador (ej: Validaciones de ViewModel, matemáticas, formateos).
2. **Espresso / Compose Test Rule**: Para pruebas de interfaz de usuario (UI Tests / Instrumented Tests). Se usan para asegurar que los botones se pueden clickear, las pantallas muestran los cuartos correctamente, etc. (Requieren un emulador o dispositivo físico).

## Estructura de las Pruebas

Android Studio divide las pruebas en dos carpetas dentro de `app/src/`:

1. `test/java/com/example/rantu/`
   - **Pruebas Unitarias Locales**: Son rapidísimas.
   - Aquí probamos funciones aisladas (ViewModels, Utils).
   
2. `androidTest/java/com/example/rantu/`
   - **Pruebas Instrumentadas (UI)**: Son más lentas pero prueban la app real.
   - Aquí probamos la navegación de Jetpack Compose y la UI.

## Comandos para Correr las Pruebas

Desde la terminal en Android Studio o usando la línea de comandos de Gradle (`./gradlew`):

- **Correr solo pruebas unitarias rápidas:**
  `./gradlew testDebugUnitTest`

- **Correr pruebas de UI (requiere emulador encendido):**
  `./gradlew connectedAndroidTest`

## Buenas Prácticas al crear Tests
1. **Convención de Nombres**: Nombrar las pruebas detallando qué prueban (ej. `fun alDarClicEnFavorito_elIconoCambiaDeColor()`).
2. **Preparar, Actuar, Verificar (Arrange, Act, Assert)**: Divide tu prueba en 3 pasos lógicos.
3. **No testear librerías**: Si usas `Coil` para imágenes o `Retrofit/Supabase` para red, no pruebes si la librería funciona, usa *Mocks* para simular las respuestas.

---
*Con esto, RANTU está listo para crecer de forma segura y robusta. Siéntete libre de ejecutar los tests cada vez que agregues un gran cambio.*
