package com.example.myviralpath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.theme.MyViralPathTheme
import androidx.compose.ui.graphics.Color
import com.example.myviralpath.ui.theme.*

// Pantalla de Onboarding - Paso 1

@Composable
fun OnboardingNichoPantalla(
    onBackClick: () -> Unit = {},
    onContinue: (niche: String, platforms: List<String>) -> Unit = { _, _ -> }
) {
    var selectedNiche by remember { mutableStateOf<String?>(null) }
    val selectedPlatforms = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header: "Paso 1 de 3", botón volver y barra de progreso
        HeaderStep(
            currentStep = "Paso 1 de 3",
            percentage = "33%",
            progress = 0.33f,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Primero, definamos tu terreno",
            color = TextoPrimario,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Esto nos ayudará a analizar oportunidades reales en tu nicho",
            color = TextoSecundario,
            fontSize = 16.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Sección: Nicho principal
        SectionTitle(icon = "△", title = "Nicho principal")

        Spacer(modifier = Modifier.height(12.dp))

        NicheGrid(
            selectedNiche = selectedNiche,
            onNicheSelected = { selectedNiche = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Sección: Plataformas objetivo
        SectionTitle(icon = "▭", title = "Plataformas objetivo")

        Spacer(modifier = Modifier.height(12.dp))

        PlatformsFlowRow(
            selectedPlatforms = selectedPlatforms,
            onPlatformToggle = { platform ->
                if (selectedPlatforms.contains(platform)) {
                    selectedPlatforms.remove(platform)
                } else {
                    selectedPlatforms.add(platform)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de acción principal
        val isEnabled = selectedNiche != null && selectedPlatforms.isNotEmpty()
        ContinuarBoton(
            enabled = isEnabled,
            onClick = {
                selectedNiche?.let { niche ->
                    onContinue(niche, selectedPlatforms.toList())
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "TOMA MENOS DE 30 SEGUNDOS",
            color = TextoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Header con paso actual, botón de volver y barra de progreso
@Composable
private fun HeaderStep(
    currentStep: String,
    percentage: String,
    progress: Float,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = currentStep,
            color = NaranjaPrimario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(BackgroundTxt)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = percentage,
            color = TextoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = BackgroundTxt,
    )
}

// Título de sección reutilizable (icono + texto)
@Composable
private fun SectionTitle(icon: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            color = TextoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

// Grid de 8 nichos, organizados en filas de 2
@Composable
private fun NicheGrid(
    selectedNiche: String?,
    onNicheSelected: (String) -> Unit
) {
    val nichos = listOf(
        "🎓" to "Educación",
        "📈" to "Fitness",
        "💼" to "Negocios",
        "⚙️" to "Tecnología",
        "🎮" to "Gaming",
        "❤" to "Lifestyle",
        "🎬" to "Entretenimiento",
        "•••" to "Otro"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        nichos.chunked(2).forEach { filaDeNichos ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                filaDeNichos.forEach { (icon, label) ->
                    NichoCarta(
                        icon = icon,
                        label = label,
                        selected = label == selectedNiche,
                        onClick = { onNicheSelected(label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Tarjeta individual de nicho
@Composable
private fun NichoCarta(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) NaranjaPrimario
                else BordeTxt,
                shape = RoundedCornerShape(28.dp)
            )
            .background(
                color = BackgroundTxt,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = if (selected) TextoPrimario
            else TextoSecundario,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// Fila de chips de plataformas, con salto de línea automático
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlatformsFlowRow(
    selectedPlatforms: List<String>,
    onPlatformToggle: (String) -> Unit
) {
    val plataformas = listOf("Instagram", "TikTok", "YouTube", "Shorts", "Reels")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        plataformas.forEach { plataforma ->
            PlatformChip(
                label = plataforma,
                selected = selectedPlatforms.contains(plataforma),
                onClick = { onPlatformToggle(plataforma) }
            )
        }
    }
}

// Chip individual de plataforma
@Composable
private fun PlatformChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) NaranjaPrimario
                else BordeTxt,
                shape = RoundedCornerShape(28.dp)
            )
            .background(
                color = BackgroundTxt,
                shape = RoundedCornerShape(28.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (selected) TextoPrimario
            else TextoSecundario,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// Botón de acción principal
@Composable
private fun ContinuarBoton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.White.copy(alpha = 0.3f),
            disabledContentColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Text(text = "Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// Vista previa de la pantalla
@Preview(showBackground = true)
@Composable
fun Pantalla1Preview() {
    MyViralPathTheme(darkTheme = true){
        OnboardingNichoPantalla()
    }
}