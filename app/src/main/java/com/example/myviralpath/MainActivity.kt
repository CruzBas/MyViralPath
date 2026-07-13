package com.example.myviralpath

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myviralpath.supabase.supabase
import com.example.myviralpath.service.AuthViewModel
import com.example.myviralpath.service.SocialAccountsViewModel
import com.example.myviralpath.ui.screens.DashboardEstrategico
import com.example.myviralpath.ui.screens.RegistrationScreen
import com.example.myviralpath.ui.screens.PantallaLogin
import com.example.myviralpath.ui.screens.TendenciasPantalla
import com.example.myviralpath.ui.screens.VinculacionCuentasScreen
import com.example.myviralpath.ui.theme.MyViralPathTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import com.example.myviralpath.ui.components.LocalSnackbarHostState
import com.example.myviralpath.ui.components.CustomSnackbar
import com.example.myviralpath.ui.theme.BackgroundOscuro
import com.example.myviralpath.ui.theme.NaranjaPrimario
import com.example.myviralpath.ui.theme.TextoPrimario
import com.example.myviralpath.ui.theme.TextoSecundario
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.status.SessionStatus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    
    private lateinit var socialViewModel: SocialAccountsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        socialViewModel = ViewModelProvider(this)[SocialAccountsViewModel::class.java]
        
        handleIntent(intent)
        enableEdgeToEdge()
        
        setContent {
            MyViralPathTheme {
                val authViewModel: AuthViewModel = viewModel()
                val sessionStatus by supabase.auth.sessionStatus.collectAsState(initial = SessionStatus.NotAuthenticated())
                val snackbarHostState = remember { SnackbarHostState() }

                CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = BackgroundOscuro,
                        snackbarHost = { 
                            SnackbarHost(snackbarHostState) { data ->
                                CustomSnackbar(data)
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            when (sessionStatus) {
                                is SessionStatus.Authenticated -> {
                                    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }
                                    
                                    MaterialTheme(
                                        colorScheme = MaterialTheme.colorScheme.copy(
                                            surfaceContainer = BackgroundOscuro,
                                            onSurfaceVariant = TextoSecundario,
                                            primary = NaranjaPrimario
                                        )
                                    ) {
                                        NavigationSuiteScaffold(
                                            navigationSuiteItems = {
                                                AppDestinations.entries.forEach {
                                                    item(
                                                        icon = { Icon(it.icon, contentDescription = it.label) },
                                                        label = { Text(it.label) },
                                                        selected = it == currentDestination,
                                                        onClick = { currentDestination = it }
                                                    )
                                                }
                                            },
                                            containerColor = BackgroundOscuro,
                                            contentColor = TextoPrimario,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            when (currentDestination) {
                                                AppDestinations.DASHBOARD -> {
                                                    val isInstagramLinked by socialViewModel.isInstagramLinked.collectAsState()
                                                    val isYoutubeLinked by socialViewModel.isYoutubeLinked.collectAsState()
                                                    val isLoading by socialViewModel.isLoading.collectAsState()
                                                    
                                                    if (isInstagramLinked || isYoutubeLinked) {
                                                        DashboardEstrategico()
                                                    } else {
                                                        VinculacionCuentasScreen(
                                                            isLoading = isLoading,
                                                            onLinkInstagram = { socialViewModel.linkInstagram() },
                                                            onLinkYoutube = { socialViewModel.linkYoutube() }
                                                        )
                                                    }
                                                }
                                                AppDestinations.TENDENCIAS -> TendenciasPantalla()
                                                else -> Greeting(
                                                    name = currentDestination.label,
                                                    onSignOut = { authViewModel.signOut() }
                                                )
                                            }
                                        }
                                    }
                                }

                                else -> {
                                    var showRegistration by rememberSaveable { mutableStateOf(false) }

                                    if (showRegistration) {
                                        RegistrationScreen(
                                            viewModel = authViewModel,
                                            onLoginClick = {
                                                showRegistration = false
                                                authViewModel.resetState()
                                            }
                                        )
                                    } else {
                                        PantallaLogin(
                                            viewModel = authViewModel,
                                            onRegistroClick = {
                                                showRegistration = true
                                                authViewModel.resetState()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        supabase.handleDeeplinks(
            intent = intent,
            onSessionSuccess = { socialViewModel.updateLinkedAccounts() }
        )
    }
}

@PreviewScreenSizes
@Composable
fun MyViralPathApp(authViewModel: AuthViewModel = viewModel()) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.DASHBOARD) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Greeting(
                name = "Android",
                modifier = Modifier.padding(innerPadding),
                onSignOut = { authViewModel.signOut() }
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    DASHBOARD("Dashboard", Icons.Rounded.Dashboard),
    TENDENCIAS("Tendencias", Icons.Rounded.TrendingUp),
    PLAN("Plan", Icons.Rounded.Description),
    PERFIL("Perfil", Icons.Rounded.Person),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onSignOut: () -> Unit = {}) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hello $name!",
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSignOut) {
                Text("Cerrar sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyViralPathTheme {
        Greeting("Android")
    }
}
