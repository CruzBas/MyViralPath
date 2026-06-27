# MyViralPath - Integración con Supabase y Autenticación

Este documento detalla los pasos realizados para integrar **Supabase** y el sistema de **Autenticación** en el proyecto Android `MyViralPath`, incluyendo las correcciones necesarias para la compatibilidad con **Android Gradle Plugin (AGP) 9.0**.

---

## 1. Configuración de Dependencias

Se agregaron las dependencias necesarias de Supabase y Ktor (cliente HTTP) en el archivo `app/build.gradle.kts`.

### Supabase BOM y Módulos
Se utiliza el Bill of Materials (BOM) para asegurar la compatibilidad de versiones:
```kotlin
dependencies {
    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.6.0"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    
    // Ktor Client (necesario para Supabase)
    implementation("io.github.jan-tennert.supabase:ktor-client-okhttp:3.1.3")
}
```

---

## 2. Inicialización de Supabase

Se creó un objeto singleton para manejar la instancia de Supabase en `app/src/main/java/com/example/myviralpath/sampledata/Supabase.kt`.

*Nota: Asegúrate de tener las credenciales correctas en tu archivo `local.properties` o en una clase de constantes.*

```kotlin
val supabase = createSupabaseClient(
    supabaseUrl = "TU_SUPABASE_URL",
    supabaseKey = "TU_SUPABASE_ANON_KEY"
) {
    install(Auth)
    install(Postgrest)
}
```

---

## 3. Implementación de la Lógica de Autenticación

### AuthViewModel
Se implementó `AuthViewModel.kt` para gestionar el estado de la sesión, el registro y el inicio de sesión utilizando corrutinas de Kotlin.

### Pantallas de UI (Compose)
1. **`PantallaLogin.kt`**: Interfaz para que los usuarios ingresen sus credenciales.
2. **`RegistrationScreen.kt`**: Interfaz para crear nuevas cuentas.

---

## 4. Gestión de Sesión en MainActivity

En `MainActivity.kt`, se implementó un observador sobre el estado de la sesión de Supabase para decidir qué pantalla mostrar:

```kotlin
val sessionStatus by supabase.auth.sessionStatus.collectAsState(initial = SessionStatus.NotAutheticated)

when (sessionStatus) {
    is SessionStatus.Authenticated -> {
        MyViralPathApp() // Pantalla principal
    }
    else -> {
        // Mostrar Login o Registro
    }
}
```

---

## 5. Corrección Crítica: Compatibilidad con AGP 9.0

Al usar **Android Gradle Plugin 9.0+**, el soporte para Kotlin viene **integrado por defecto**. Intentar aplicar manualmente el plugin de Kotlin genera el error:
> *Cannot add extension with name 'kotlin', as there is an extension already registered with that name.*

### Pasos realizados para solucionar el error:

1. **Eliminación del plugin redundante en `libs.versions.toml`**:
   Se eliminó la línea `kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }`.

2. **Limpieza en `build.gradle.kts` (Proyecto y App)**:
   Se eliminó la aplicación explícita de `alias(libs.plugins.kotlin.android)`. AGP 9.0 ahora maneja la extensión `kotlin` automáticamente.

3. **Limpieza de dependencias duplicadas**:
   Se eliminaron referencias repetidas a `core-ktx` y `lifecycle-runtime-ktx` para mantener un archivo de configuración limpio y eficiente.

---

## Cómo ejecutar el proyecto
1. Realiza un **Gradle Sync**.
2. Asegúrate de que el emulador o dispositivo físico tenga acceso a internet.
3. Ejecuta la aplicación. La pantalla de login aparecerá automáticamente si no hay una sesión activa.
