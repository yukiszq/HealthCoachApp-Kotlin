package com.example.assignment.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlin.time.Duration.Companion.seconds

/**
 * Holds the single Supabase client for the whole app (remote data storage).
 * This mirrors the top-level `supabase` client in Practical 9.
 *
 * HOW TO CONFIGURE (Practical 9, steps 9-15):
 *   1. Create a Supabase project.
 *   2. Project Settings -> API Keys -> create a **Secret key**.
 *   3. Top bar -> Connect -> Mobile Frameworks -> Android Kotlin -> copy the Project URL.
 *   4. Paste both values below.
 *
 * Until real values are pasted, [isConfigured] is false and the app simply
 * skips every remote call and keeps working with Room only.
 */
object SupabaseModule {

    private const val SUPABASE_URL = "https://wkuhybginfhgklakpeum.supabase.co"
    private const val SUPABASE_KEY = "sb_secret_yWtlYDV4PZIbj3zgOGJ1_w_LHGK_i5-"

    /** True once real credentials have been pasted in above. */
    val isConfigured: Boolean =
        SUPABASE_URL.startsWith("https://") && SUPABASE_KEY.length > 20

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            // Don't let a slow / unreachable server hang the app for long.
            requestTimeout = 8.seconds
            install(Postgrest)
        }
    }
}
