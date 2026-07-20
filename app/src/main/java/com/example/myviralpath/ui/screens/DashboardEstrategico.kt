package com.example.myviralpath.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myviralpath.R
import com.example.myviralpath.service.DashboardUiState
import com.example.myviralpath.service.DashboardViewModel
import com.example.myviralpath.service.RecentVideo
import com.example.myviralpath.service.YoutubeStats
import com.example.myviralpath.ui.theme.*
import com.example.myviralpath.ui.components.LocalSnackbarHostState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun DashboardEstrategico(dashboardViewModel: DashboardViewModel = viewModel()) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()
    val uiState by dashboardViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        item { SeccionHeader() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { EstadoIndicador(uiState) }
        item { Spacer(modifier = Modifier.height(32.dp)) }

        when (uiState) {
            is DashboardUiState.Loading -> {
                item { LoadingCard() }
            }
            is DashboardUiState.Error -> {
                item {
                    ErrorCard(
                        message = (uiState as DashboardUiState.Error).message,
                        onRetry = { dashboardViewModel.retry() }
                    )
                }
            }
            is DashboardUiState.NotLinked -> {
                item {
                    NoVinculadoCard(
                        onVerDetallesClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Vincula tu cuenta de Google/YouTube en Ajustes")
                            }
                        }
                    )
                }
            }
            is DashboardUiState.Success -> {
                val stats = (uiState as DashboardUiState.Success).stats

                item {
                    TarjetaCanalYoutube(stats = stats)
                }
                item { Spacer(modifier = Modifier.height(30.dp)) }
                item { SeccionTitulo("Metricas Estrategicas") }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { MetricasReales(stats = stats) }
                item { Spacer(modifier = Modifier.height(30.dp)) }
                item { SeccionTitulo("Videos Recientes", mostrarFlecha = true) }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { TendenciasReales(videos = stats.recentVideos) }
                item { Spacer(modifier = Modifier.height(40.dp)) }
                item { SeccionTitulo("ProximosPasos") }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { ProximosPasosReales(stats = stats) }
            }
        }
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────

@Composable
fun SeccionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hola Sebastian",
                color = TextoPrimario,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu estrategia de crecimiento \nesta lista",
                color = TextoSecundario,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_account_box),
            contentDescription = "Perfil",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(BackgroundTxt)
                .padding(8.dp)
        )
    }
}

// ─── Estado ──────────────────────────────────────────────────────────────────

@Composable
fun EstadoIndicador(uiState: DashboardUiState) {
    val (color, texto) = when (uiState) {
        is DashboardUiState.Loading  -> Color(0xFFFFB300) to "Cargando datos de YouTube..."
        is DashboardUiState.Success  -> Color(0xFF4CAF50) to "Datos de YouTube actualizados"
        is DashboardUiState.Error    -> Color.Red to "Error al cargar YouTube"
        is DashboardUiState.NotLinked -> Color(0xFF9E9E9E) to "Canal de YouTube no vinculado"
    }

    Row(
        modifier = Modifier
            .background(BackgroundTxt, RoundedCornerShape(20.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto, color = TextoSecundario, fontSize = 12.sp)
    }
}

// ─── Loading ─────────────────────────────────────────────────────────────────

@Composable
fun LoadingCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(24.dp))
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = NaranjaPrimario, strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Obteniendo datos de YouTube...",
            color = TextoSecundario,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ─── Error ───────────────────────────────────────────────────────────────────

@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_favorite),
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Error al cargar datos", color = TextoPrimario, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = TextoSecundario, fontSize = 12.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text("Reintentar", color = TextoPrimario, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Not Linked ───────────────────────────────────────────────────────────────

@Composable
fun NoVinculadoCard(onVerDetallesClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "Canal de YouTube",
            color = TextoPrimario,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Vincula tu cuenta de Google para ver tus métricas reales de YouTube en este dashboard.",
            color = TextoSecundario,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        PlanItem(R.drawable.ic_home, "Suscriptores en tiempo real")
        Spacer(modifier = Modifier.height(12.dp))
        PlanItem(R.drawable.ic_launcher_foreground, "Vistas y rendimiento de videos")
        Spacer(modifier = Modifier.height(12.dp))
        PlanItem(R.drawable.ic_favorite, "Análisis de tendencias")
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onVerDetallesClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario)
        ) {
            Text("Vincular YouTube", color = TextoPrimario, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Canal YouTube Card ───────────────────────────────────────────────────────

@Composable
fun TarjetaCanalYoutube(stats: YoutubeStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = null,
                tint = Color(0xFFFF0000),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stats.channelTitle.ifEmpty { "Mi Canal" },
                    color = TextoPrimario,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Canal de YouTube",
                    color = TextoSecundario,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Stats row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatItem(
                modifier = Modifier.weight(1f),
                label = "Suscriptores",
                value = DashboardViewModel.formatCount(stats.subscriberCount)
            )
            StatItem(
                modifier = Modifier.weight(1f),
                label = "Vistas Totales",
                value = DashboardViewModel.formatCount(stats.viewCount)
            )
            StatItem(
                modifier = Modifier.weight(1f),
                label = "Videos",
                value = stats.videoCount.toString()
            )
        }

        if (stats.topVideoTitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = BordeTxt, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "🏆 Video más visto", color = TextoSecundario, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stats.topVideoTitle,
                color = TextoPrimario,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Text(
                text = "${DashboardViewModel.formatCount(stats.topVideoViews)} vistas",
                color = NaranjaPrimario,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatItem(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .background(BackgroundOscuro.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, color = TextoPrimario, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, color = TextoSecundario, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}

// ─── Metricas Reales ─────────────────────────────────────────────────────────

@Composable
fun MetricasReales(stats: YoutubeStats) {
    val avgViews = DashboardViewModel.formatCount(stats.avgViewsPerVideo)
    val engagementLabel = when {
        stats.avgViewsPerVideo > 100_000 -> "Muy Alto"
        stats.avgViewsPerVideo > 10_000  -> "Alto"
        stats.avgViewsPerVideo > 1_000   -> "Medio"
        else                              -> "En crecimiento"
    }
    val potencial = when {
        stats.subscriberCount > 100_000 -> "Muy Alto"
        stats.subscriberCount > 10_000  -> "Alto"
        stats.subscriberCount > 1_000   -> "Medio"
        else                             -> "Emergente"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaMetrica(modifier = Modifier.weight(1f), iconId = R.drawable.ic_home,            valor = avgViews,        label = "Avg. Vistas")
        TarjetaMetrica(modifier = Modifier.weight(1f), iconId = R.drawable.ic_account_box,     valor = engagementLabel, label = "Engagement")
        TarjetaMetrica(modifier = Modifier.weight(1f), iconId = R.drawable.ic_launcher_foreground, valor = potencial,   label = "Potencial")
    }
}

// ─── Videos Recientes ────────────────────────────────────────────────────────

@Composable
fun TendenciasReales(videos: List<RecentVideo>) {
    if (videos.isEmpty()) {
        Text(text = "Sin videos recientes", color = TextoSecundario, fontSize = 13.sp)
        return
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(videos.size) { idx ->
            val video = videos[idx]
            val progreso = if (videos.maxOfOrNull { it.views } ?: 0 > 0) {
                video.views.toFloat() / (videos.maxOf { it.views }.toFloat())
            } else 0f

            TarjetaTendencia(
                textoInsignia = DashboardViewModel.formatCount(video.views) + " vistas",
                colorInsignia = if (idx == 0) Color(0xFF2E7D32) else Color(0xFF1565C0),
                titulo = video.title,
                progreso = progreso
            )
        }
    }
}

// ─── Proximos Pasos Reales ────────────────────────────────────────────────────

@Composable
fun ProximosPasosReales(stats: YoutubeStats) {
    val pasoTexto = when {
        stats.videoCount == 0        -> "Sube tu primer video para empezar"
        stats.avgViewsPerVideo < 500 -> "Optimiza tus títulos y miniaturas"
        stats.subscriberCount < 1000 -> "Impulsa tu canal con Shorts frecuentes"
        else                          -> "Analiza el hashtag de tu nicho principal"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(16.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, NaranjaPrimario, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                tint = NaranjaPrimario,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = pasoTexto, color = TextoPrimario, fontSize = 14.sp)
    }
}

// ─── Componentes reutilizados ─────────────────────────────────────────────────

@Composable
fun PlanItem(iconId: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = NaranjaPrimario,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = TextoSecundario, fontSize = 14.sp)
    }
}

@Composable
fun SeccionTitulo(titulo: String, mostrarFlecha: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            color = TextoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        if (mostrarFlecha) {
            Text(text = "->", color = TextoSecundario, fontSize = 16.sp)
        }
    }
}

@Composable
fun TarjetaMetrica(modifier: Modifier = Modifier, iconId: Int, valor: String, label: String) {
    Column(
        modifier = modifier
            .background(Color.Transparent, RoundedCornerShape(16.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = NaranjaPrimario,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = valor, color = TextoPrimario, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = TextoSecundario, fontSize = 11.sp)
    }
}

@Composable
fun TarjetaTendencia(textoInsignia: String, colorInsignia: Color, titulo: String, progreso: Float) {
    Column(
        modifier = Modifier
            .width(180.dp)
            .background(BackgroundTxt, RoundedCornerShape(16.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(colorInsignia.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = null,
                tint = colorInsignia,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = textoInsignia, color = colorInsignia, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = titulo, color = TextoPrimario, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2)
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = NaranjaPrimario,
            trackColor = BordeTxt
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
fun DashboardEstrategicoPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    MyViralPathTheme {
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            DashboardEstrategico()
        }
    }
}