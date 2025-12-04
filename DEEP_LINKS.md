# Deep Links y Compartir en RANTU

## 📱 Configuración de Deep Links

La aplicación RANTU soporta múltiples tipos de deep links para abrir cuartos específicos desde la web o desde otras aplicaciones.

### Formatos Soportados

#### 1. **URLs Web** (Auto-verificadas)

```
https://prototype-delta-vert.vercel.app/cuarto/[id]
http://localhost:4321/cuarto/[id]
```

Estos links abren automáticamente la app si está instalada, o redirigen a la web si no lo está.

#### 2. **Deep Links Personalizados**

```
rantu://cuarto/[id]
```

Este formato es específico de la app y siempre intentará abrirla.

#### 3. **Magic Links de Autenticación**

```
rantu://login
```

Para el flujo de autenticación con Supabase.

## 🔗 Configuración en Android

### AndroidManifest.xml

```xml
<!-- Deep Links para URLs Web -->
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />

    <data android:scheme="https"
          android:host="prototype-delta-vert.vercel.app" />
    <data android:scheme="http"
          android:host="localhost"
          android:port="4321" />
</intent-filter>

<!-- Deep Links Personalizados -->
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />

    <data android:scheme="rantu"
          android:host="cuarto"
          android:pathPrefix="/" />
</intent-filter>
```

### MainActivity.kt

El `MainActivity` procesa los deep links en dos lugares:

1. **onCreate()**: Para cuando la app se abre desde un deep link
2. **onNewIntent()**: Para cuando la app ya está abierta

```kotlin
private fun handleDeepLink(intent: Intent?): Int? {
    val data: Uri? = intent?.data

    return when {
        // https://prototype-delta-vert.vercel.app/cuarto/123
        data?.host == "prototype-delta-vert.vercel.app" &&
        data.pathSegments.getOrNull(0) == "cuarto" -> {
            data.pathSegments.getOrNull(1)?.toIntOrNull()
        }

        // rantu://cuarto/123
        data?.scheme == "rantu" && data.host == "cuarto" -> {
            data.pathSegments.firstOrNull()?.toIntOrNull()
        }

        else -> null
    }
}
```

## 📤 Compartir Cuartos

### Desde la Lista de Cuartos

Cada tarjeta de cuarto incluye un botón de compartir (ícono de compartir) que permite:

- Compartir por WhatsApp, Telegram, Email, etc.
- Incluye tanto el link web como el deep link de la app
- Muestra título, precio y descripción del cuarto

```kotlin
onShareClick = { roomId ->
    val webUrl = "https://prototype-delta-vert.vercel.app/cuarto/$roomId"
    val deepLinkUrl = "rantu://cuarto/$roomId"
    val shareText = """
        🏠 ${room.title}
        💰 $${room.price}/mes

        Ver más detalles: $webUrl
        O abre en la app: $deepLinkUrl
    """.trimIndent()

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    context.startActivity(Intent.createChooser(shareIntent, "Compartir cuarto via"))
}
```

### Desde Detalles del Cuarto

En la pantalla de detalles hay dos botones principales:

#### 1. **Botón de WhatsApp** 🟢

- Contacta directamente al dueño del cuarto
- Mensaje pre-formateado con información del cuarto
- Solo habilitado si el cuarto tiene número de WhatsApp

```kotlin
Button(
    onClick = {
        val mensaje = "Hola, me interesa el cuarto: ${room.title} - $${room.price}/mes. " +
                "Lo vi en RANTU. ¿Está disponible?"
        val whatsappUrl = "https://wa.me/$celular?text=${URLEncoder.encode(mensaje, "UTF-8")}"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(whatsappUrl)
        }
        context.startActivity(intent)
    }
) {
    Icon(...)
    Text("WhatsApp")
}
```

#### 2. **Botón de Compartir** 🔗

- Comparte la tarjeta del cuarto completa
- Incluye imagen, título, precio y descripción
- Deep link + Web link incluidos

## 🌐 Integración Web

### Página de Cuarto en Astro

La página web en `/cuarto/[id].astro` debe incluir meta tags para mejor compartición:

```html
<head>
  <!-- Open Graph para redes sociales -->
  <meta property="og:title" content="{cuarto.titulo}" />
  <meta property="og:description" content="{cuarto.descripcion}" />
  <meta property="og:image" content="{cuarto.imagen_1}" />
  <meta property="og:url"
  content={`https://prototype-delta-vert.vercel.app/cuarto/${id}`} />

  <!-- Deep Link alternativo -->
  <meta property="al:android:url" content={`rantu://cuarto/${id}`} />
  <meta property="al:android:package" content="com.example.rantu" />
  <meta property="al:android:app_name" content="RANTU" />
</head>
```

### Smart Banner (Opcional)

Para promover la instalación de la app desde la web:

```html
<meta name="apple-itunes-app" content="app-id=myAppStoreID" />
<meta name="google-play-app" content="app-id=com.example.rantu" />
```

## 🧪 Pruebas

### Probar Deep Links en Android

#### Desde ADB:

```bash
# Deep link personalizado
adb shell am start -W -a android.intent.action.VIEW -d "rantu://cuarto/123" com.example.rantu

# URL web
adb shell am start -W -a android.intent.action.VIEW -d "https://prototype-delta-vert.vercel.app/cuarto/123" com.example.rantu
```

#### Desde HTML de Prueba:

```html
<!DOCTYPE html>
<html>
  <body>
    <h1>Probar Deep Links RANTU</h1>

    <!-- Deep link directo -->
    <a href="rantu://cuarto/1">Abrir Cuarto 1 (App)</a>
    <br /><br />

    <!-- URL web -->
    <a href="https://prototype-delta-vert.vercel.app/cuarto/1"
      >Abrir Cuarto 1 (Web/App)</a
    >

    <script>
      // Intento automático de abrir app, fallback a web
      function openRoom(roomId) {
        const deepLink = `rantu://cuarto/${roomId}`;
        const webLink = `https://prototype-delta-vert.vercel.app/cuarto/${roomId}`;

        window.location = deepLink;

        // Fallback a web después de 2 segundos
        setTimeout(() => {
          window.location = webLink;
        }, 2000);
      }
    </script>
  </body>
</html>
```

## 📊 Flujo de Navegación

```
Usuario recibe link → Sistema detecta formato
                             ↓
                    ¿App instalada?
                    ↙         ↘
                  Sí          No
                   ↓           ↓
            Abre App      Abre Web
                   ↓
         Deep Link Handler
                   ↓
        Busca cuarto por ID
                   ↓
          ¿Encontrado?
          ↙         ↘
        Sí          No
         ↓           ↓
   Muestra detalles  Error 404
```

## 🔐 Consideraciones de Seguridad

1. **Validación de IDs**: Siempre valida que el ID del cuarto sea un número válido
2. **Verificación de Existencia**: Verifica que el cuarto exista antes de mostrarlo
3. **Auto-verificación**: Usa `android:autoVerify="true"` para verificación automática de dominios
4. **HTTPS**: Usa siempre HTTPS en producción

## 📈 Analytics (Recomendado)

Para trackear el uso de deep links:

```kotlin
// En handleDeepLink()
private fun handleDeepLink(intent: Intent?): Int? {
    val roomId = // ... extraer ID

    if (roomId != null) {
        // Log evento de deep link
        analyticsService.logEvent("deep_link_opened", mapOf(
            "room_id" to roomId,
            "source" to when {
                intent?.data?.scheme == "rantu" -> "app_share"
                else -> "web"
            }
        ))
    }

    return roomId
}
```

## 🚀 Próximas Mejoras

- [ ] Branch.io para deep links universales
- [ ] Firebase Dynamic Links
- [ ] QR codes para cuartos
- [ ] Compartir en stories de Instagram
- [ ] Preview de tarjetas enriquecidas
- [ ] Deep links para búsquedas filtradas
- [ ] Deep links para perfiles de usuarios
