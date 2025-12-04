# Implementación de CRUD de Cuartos - App Android RANTU

## ✅ Funcionalidades Implementadas

### 1. **Ver Mis Cuartos** (MyRoomsScreen)

- Lista de cuartos del usuario autenticado
- Estadísticas: Total de cuartos, activos, precio promedio
- Botones para editar y eliminar cada cuarto
- FloatingActionButton para agregar nuevo cuarto

### 2. **Agregar Cuarto** (AddRoomScreen)

- Formulario completo con todos los campos:
  - Título del cuarto \*
  - Descripción \*
  - Precio mensual \*
  - Número de celular (WhatsApp) \*
  - Características (mínimo 20 caracteres) \*
  - Ubicación específica (mínimo 10 caracteres) \*
  - Hasta 3 imágenes (la primera es obligatoria)
- Validaciones en tiempo real
- Subida de imágenes a Supabase Storage
- Indicador de progreso durante la subida

### 3. **Editar Cuarto** (EditRoomScreen)

- Formulario pre-poblado con datos existentes
- Permite actualizar todos los campos
- Muestra imágenes actuales
- Permite cambiar imágenes (opcional)
- Validaciones al guardar

### 4. **Eliminar Cuarto**

- Confirmación antes de eliminar
- Eliminación lógica (marca como inactivo)
- Actualización automática de la lista

## 📁 Archivos Creados/Modificados

### Nuevos Archivos

1. **AddRoomScreen.kt** - Pantalla para agregar cuartos
2. **EditRoomScreen.kt** - Pantalla para editar cuartos
3. **AddRoomViewModel.kt** - Lógica para agregar cuartos
4. **EditRoomViewModel.kt** - Lógica para editar cuartos

### Archivos Modificados

1. **InterfaceFirst.kt** - Navegación entre pantallas
2. **MyRoomsScreen.kt** - Ya existía, funciona con las nuevas pantallas
3. **RoomRepository.kt** - Ya tenía los métodos de CRUD
4. **UserRoomsViewModel.kt** - Ya tenía la lógica para cargar y eliminar
5. **SupabaseClient.kt** - Agregado módulo Storage
6. **build.gradle.kts** - Agregada dependencia storage-kt
7. **AndroidManifest.xml** - Agregados permisos para leer imágenes

## 🎯 Flujo de Usuario

### Agregar un Cuarto

1. Usuario hace clic en el botón FAB (+) en "Mis Cuartos"
2. Se abre AddRoomScreen con formulario vacío
3. Usuario llena todos los campos obligatorios
4. Usuario selecciona al menos una imagen
5. Usuario hace clic en "Agregar Cuarto"
6. La app valida los datos
7. Sube las imágenes a Supabase Storage
8. Guarda el cuarto en la base de datos
9. Redirige a "Mis Cuartos" con el cuarto agregado

### Editar un Cuarto

1. Usuario hace clic en "Editar" en un cuarto
2. Se abre EditRoomScreen con datos pre-poblados
3. Usuario modifica los campos que desea
4. Usuario puede cambiar imágenes (opcional)
5. Usuario hace clic en "Actualizar Cuarto"
6. La app valida los datos
7. Sube nuevas imágenes si se seleccionaron
8. Actualiza el cuarto en la base de datos
9. Redirige a "Mis Cuartos" con cambios reflejados

### Eliminar un Cuarto

1. Usuario hace clic en "Eliminar" en un cuarto
2. Se muestra diálogo de confirmación
3. Usuario confirma la eliminación
4. El cuarto se marca como inactivo (soft delete)
5. Se actualiza la lista automáticamente

## 🔧 Configuración Técnica

### Dependencias Requeridas

```kotlin
implementation("io.github.jan-tennert.supabase:postgrest-kt:2.5.1")
implementation("io.github.jan-tennert.supabase:gotrue-kt:2.5.1")
implementation("io.github.jan-tennert.supabase:storage-kt:2.5.1") // Nueva
```

### Permisos en AndroidManifest

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

### Configuración de Supabase

```kotlin
install(Storage) // Agregado al SupabaseClient
```

## 📊 Estructura de Datos

### RoomInsert (para crear)

```kotlin
data class RoomInsert(
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val celular: String?,
    val caracteristicas: String?,
    val ubicacion: String?,
    val imagen_1: String?,
    val imagen_2: String?,
    val imagen_3: String?,
    val user_id: String,
    val activo: Boolean = true
)
```

### RoomUpdate (para editar)

```kotlin
data class RoomUpdate(
    val titulo: String?,
    val descripcion: String?,
    val precio: Double?,
    val celular: String?,
    val caracteristicas: String?,
    val ubicacion: String?,
    val imagen_1: String?,
    val imagen_2: String?,
    val imagen_3: String?
)
```

## 🎨 Componentes Reutilizables

### ImagePicker

Componente para seleccionar imágenes con preview:

- Área de carga con diseño atractivo
- Preview de imagen seleccionada
- Botón para eliminar imagen

### EditableImagePicker

Versión extendida para edición:

- Muestra imagen existente
- Permite seleccionar nueva imagen
- Indicador visual de "Nueva" imagen
- Botón para cambiar imagen

## ✨ Características Destacadas

1. **Validaciones Completas**

   - Todos los campos obligatorios validados
   - Validaciones de longitud mínima
   - Validación de formato de teléfono
   - Validación de tamaño de imágenes (5MB max)

2. **Experiencia de Usuario**

   - Indicadores de progreso durante subidas
   - Mensajes de error claros y específicos
   - Confirmaciones antes de acciones destructivas
   - Actualización automática de listas

3. **Manejo de Imágenes**

   - Subida a Supabase Storage
   - Nombres únicos con timestamp
   - Preview antes de subir
   - Manejo de errores de red

4. **Seguridad**
   - Verificación de autenticación
   - Verificación de permisos (solo el dueño puede editar/eliminar)
   - Soft delete (mantiene datos históricos)

## 🚀 Cómo Usar

1. **Compilar el proyecto**

   ```bash
   ./gradlew build
   ```

2. **Ejecutar en dispositivo**
   - Asegúrate de tener permisos de almacenamiento habilitados
   - Inicia sesión con tu cuenta
   - Navega a "Mis Cuartos" desde el menú de perfil
   - Usa el botón FAB (+) para agregar cuartos

## 📝 Notas Importantes

- Las imágenes se suben a `cuartos-images` bucket en Supabase Storage
- El bucket debe estar configurado como público para ver las imágenes
- La primera imagen siempre se usa como imagen principal
- Los cuartos eliminados se marcan como `activo = false` (no se borran)

## 🔄 Sincronización con Web

Esta implementación es compatible con la versión web en `prototype`:

- Misma estructura de base de datos
- Mismo sistema de autenticación
- Mismo bucket de Storage
- Los cuartos creados desde la app aparecen en la web y viceversa

## 🐛 Troubleshooting

### Las imágenes no se cargan

- Verifica que el bucket `cuartos-images` existe en Supabase
- Verifica que el bucket es público
- Revisa los logs de Android Studio

### Error de permisos al crear/editar

- Verifica que el usuario está autenticado
- Verifica las políticas RLS en Supabase
- El user_id debe coincidir con el propietario del cuarto

### Error al seleccionar imágenes

- Verifica que los permisos están en AndroidManifest.xml
- Solicita permisos en tiempo de ejecución si es necesario (Android 13+)
