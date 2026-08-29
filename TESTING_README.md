# 🧪 Guía Oficial de Testing - RANTU Android

Esta guía detalla la implementación de pruebas automatizadas y la arquitectura testeable (Clean Architecture) de la aplicación Android.

## 🛠️ Herramientas Utilizadas
- **JUnit 4**: Framework estándar para ejecución de pruebas en la JVM (Java Virtual Machine).
- **MockK**: Framework de mocking moderno para Kotlin.
- **Coroutines Test**: Utilidades para probar funciones `suspend` y flujos asíncronos (`StateFlow`).

## 📂 ¿Dónde están las pruebas?
Las pruebas unitarias viven en la carpeta del código fuente de testing local:
`app/src/test/java/com/example/rantu/...`

## 🚀 Ejecutar las Pruebas

Tienes dos opciones principales para ejecutar los tests sin necesidad de un dispositivo físico o emulador:

**Opción 1: Interfaz Gráfica (Android Studio)**
1. Abre el panel "Project" (lado izquierdo).
2. Expande `app/src/test/java/com/example/rantu`.
3. Haz clic derecho sobre la carpeta entera o un archivo de prueba específico.
4. Selecciona **"Run Tests in..."** o haz clic en los íconos verdes de "Play" al lado del código de prueba.

**Opción 2: Línea de Comandos (Terminal)**
```bash
./gradlew testDebugUnitTest
```

## 📝 Casos de Prueba Implementados

1. **`RoomDetailViewModelTest.kt` (Pruebas Asíncronas y de Inyección)**
   - **Simulación de Repositorio (Mocking):** Usa `MockK` para simular llamadas de red lentas.
   - **Verificación de Estados:** Comprueba que al iniciar la petición el estado `isLoading` sea `true`, y al finalizar se asigne correctamente la data a `room.value`.
   - **Manejo de Errores:** Simula una caída del servidor (Exception) y verifica que la interfaz reciba un `errorMsg` amigable en lugar de cerrarse abruptamente (Crash).

2. **`RoomFormValidatorTest.kt` (Pruebas de Lógica de Negocio)**
   - **Boundary Testing:** Se prueban las restricciones de longitud (por ejemplo, celular con menos de 10 dígitos o características cortas).
   - **Validación Limpia:** Confirma que si todos los datos son válidos, el validador retorne nulo (ausencia de errores).

## 🛡️ Beneficios para el Proyecto
Gracias a que eliminamos el instanciamiento rígido de las bases de datos (implementamos **Dependency Injection**) y separamos la lógica compleja a objetos puros, ahora Android es completamente testeable. Cualquier estudiante en el futuro podrá modificar los ViewModels teniendo la confianza de que las pruebas automatizadas le avisarán si rompe algo.
