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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.R
import com.example.myviralpath.ui.theme.*
import com.example.myviralpath.ui.components.LocalSnackbarHostState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun DashboardEstrategico() {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    // LazyColumn para que se pueda scrollear
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        item { SeccionHeader() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { Estado() }
        item { Spacer(modifier = Modifier.height(32.dp)) }
        item { TarjetaPlanEstrategico(
            onVerDetallesClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Optimizar estrategia")
                }
            }
        ) }
        item { Spacer(modifier = Modifier.height(30.dp)) }
        item { SeccionTitulo("Metricas Estrategicas") }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { Metricas() }
        item { Spacer(modifier = Modifier.height(30.dp)) }
        item { SeccionTitulo("Tendencias", mostrarFlecha = true) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { Tendencias() }
        item { Spacer(modifier = Modifier.height(40.dp)) }
        item { SeccionTitulo("ProximosPasos") }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { ProximosPasos() }
    }
}

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

        // Avatar del usuario
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

@Composable
fun Estado() {
    Row(
        modifier = Modifier
            .background(BackgroundTxt, RoundedCornerShape(20.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Punto rojo de Compose
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Red, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "IA actualizando datos en tiempo real",
            color = TextoSecundario,
            fontSize = 12.sp,
        )
    }
}

@Composable
fun TarjetaPlanEstrategico(onVerDetallesClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Text(
            text = "Plan Estrategico de hoy",
            color = TextoPrimario,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Items del plan
        PlanItem(R.drawable.ic_home, "3 ideas de contenido personalizadas")
        Spacer(modifier = Modifier.height(12.dp))
        PlanItem(R.drawable.ic_launcher_foreground, "Mejor horario: 7:30 PM")
        Spacer(modifier = Modifier.height(12.dp))
        PlanItem(R.drawable.ic_favorite, "Plataforma: Reels")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerDetallesClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario)
        ) {
            Text("Ver detalles", color = TextoPrimario, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

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
fun Metricas() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaMetrica(modifier = Modifier.weight(1f), iconId = R.drawable.ic_home, valor = "82%", label = "Oportunidad")
        TarjetaMetrica(modifier = Modifier.weight(1f), iconId = R.drawable.ic_account_box, valor = "Media", label = "Competencia")
        TarjetaMetrica(modifier = Modifier.weight(1f), iconId = R.drawable.ic_launcher_foreground, valor = "Alto", label = "Potencial")
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
fun Tendencias() {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            TarjetaTendencia(
                textoInsignia = "Rising",
                colorInsignia = Color(0xFF2E7D32),
                titulo = "HIIT Challenge",
                progreso = 0.7f
            )
        }
        item {
            TarjetaTendencia(
                textoInsignia = "Stable",
                colorInsignia = Color(0xFF1565C0),
                titulo = "Meal prep Hacks",
                progreso = 0.4f
            )
        }
    }
}

@Composable
fun TarjetaTendencia(textoInsignia: String, colorInsignia: Color, titulo: String, progreso: Float) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .background(BackgroundTxt, RoundedCornerShape(16.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Insignia
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

        Text(text = titulo, color = TextoPrimario, fontSize = 14.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de progreso nativa
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = NaranjaPrimario,
            trackColor = BordeTxt
        )
    }
}

@Composable
fun ProximosPasos() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(16.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Check Box simulada
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
        Text(text = "Analizar hastag #FitnessPro", color = TextoPrimario, fontSize = 14.sp)
    }
}

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