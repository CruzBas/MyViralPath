package com.example.myviralpath.supabase

import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

// Es recomendable usar una sola instancia del cliente en toda la app
val supabase = createSupabaseClient(
    supabaseUrl = "https://qalcenruzfnytwfkhumb.supabase.co",
    supabaseKey="sb_publishable_4OqlD6LlW0sugcNGoVl66Q_kgx7iWB9"// Reemplaza con tu clave anon de Supabase
) {
    install(Postgrest)
    install(Auth) {

        autoLoadFromStorage = true
        scheme = "myviralpath"
        host = "login-callback"
    }
    install(ComposeAuth)
}
