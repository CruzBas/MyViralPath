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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import com.example.myviralpath.ui.theme.MyViralPathTheme

// Pantalla de Onboarding Paso audiencia ideal

@Composable
fun AudienciaPantalla(
    onBackClick: () -> Unit = {},
    onFinish: (countryName: String, ageMin: Int, ageMax: Int, gender: String) -> Unit = { _, _, _, _ -> }
) {
    var countryName by remember { mutableStateOf("") }
    var ageRange by remember { mutableStateOf(18f..35f) }
    var selectedGender by remember { mutableStateOf("MIXTO") }

    // Contenedor principal: apila todo verticalmente y permite scroll
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Encabezado con progreso
        HeaderStep2(
            currentStep = "PASO 2 DE 3",
            percentage = "66% Completado",
            progress = 0.66f,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Título de la pantalla
        Text(
            text = "Ahora, hablemos de tu audiencia ideal",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subtítulo descriptivo
        Text(
            text = "ViralPath detectará tendencias basadas en este perfil para optimizar tu alcance.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Sección: país o región
        SectionLabel(text = "PAÍS O REGIÓN")

        Spacer(modifier = Modifier.height(10.dp))

        BuscarPaisField(
            texto = countryName,
            onTextoChange = { countryName = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        RegionChipsRow(
            selectedCountry = countryName,
            onCountrySelected = { countryName = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Sección: rango de edad
        RangoEdadSection(
            rangoEdad = ageRange,
            onRangoEdadChange = { ageRange = it }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Sección: género objetivo
        SectionLabel(text = "GÉNERO OBJETIVO")

        Spacer(modifier = Modifier.height(10.dp))

        GeneroObjetivoRow(
            selectedGender = selectedGender,
            onGenderSelected = { selectedGender = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de acción principal
        ContinuarBoton(
            onClick = {
                onFinish(
                    countryName,
                    ageRange.start.toInt(),
                    ageRange.endInclusive.toInt(),
                    selectedGender
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Texto de apoyo final con icono
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Estamos construyendo tu perfil estratégico",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Header con paso actual, avatar y porcentaje completado
@Composable
private fun HeaderStep2(
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
        // Botón circular de volver
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
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
            text = currentStep,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = percentage,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Barra de progreso
    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

// Etiqueta pequeña de sección
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp
    )
}

// Campo de búsqueda de país
@Composable
private fun BuscarPaisField(texto: String, onTextoChange: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        placeholder = { Text(text = "Buscar país...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

// Fila de chips con banderas de países/regiones
@Composable
private fun RegionChipsRow(selectedCountry: String, onCountrySelected: (String) -> Unit) {
    val regiones = listOf("🇪🇸" to "España", "🇲🇽" to "México", "🌐" to "Global (EN)")

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        regiones.forEach { (bandera, nombre) ->
            RegionChip(
                bandera = bandera,
                nombre = nombre,
                selected = nombre == selectedCountry,
                onClick = { onCountrySelected(nombre) }
            )
        }
    }
}

// Chip individual de región
@Composable
private fun RegionChip(bandera: String, nombre: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(10.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = bandera, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = nombre,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}

// Sección de rango de edad: etiqueta + valor + slider + marcas
@Composable
private fun RangoEdadSection(
    rangoEdad: ClosedFloatingPointRange<Float>,
    onRangoEdadChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SectionLabel(text = "RANGO DE EDAD")
        Text(
            text = "${rangoEdad.start.toInt()} — ${rangoEdad.endInclusive.toInt()} años",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }

    RangeSlider(
        value = rangoEdad,
        onValueChange = onRangoEdadChange,
        valueRange = 13f..65f,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.primary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )

    // Marcas de referencia debajo del slider
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("13", "25", "40", "55", "65+").forEach { marca ->
            Text(text = marca, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

// Fila de 3 tarjetas de género objetivo
@Composable
private fun GeneroObjetivoRow(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GeneroCard(
            icon = Icons.Default.Groups,
            label = "MIXTO",
            selected = "MIXTO" == selectedGender,
            onClick = { onGenderSelected("MIXTO") },
            modifier = Modifier.weight(1f)
        )
        GeneroCard(
            icon = Icons.Default.Female,
            label = "FEMENINO",
            selected = "FEMENINO" == selectedGender,
            onClick = { onGenderSelected("FEMENINO") },
            modifier = Modifier.weight(1f)
        )
        GeneroCard(
            icon = Icons.Default.Male,
            label = "MASCULINO",
            selected = "MASCULINO" == selectedGender,
            onClick = { onGenderSelected("MASCULINO") },
            modifier = Modifier.weight(1f)
        )
    }
}

// Tarjeta individual de género (icono arriba, texto abajo)
@Composable
private fun GeneroCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Botón de acción principal
@Composable
private fun ContinuarBoton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(text = "Finalizar Configuración", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Vista previa de la pantalla
@Preview(showBackground = true)
@Composable
fun Pantalla2Preview() {
    MyViralPathTheme(darkTheme = true) {
        AudienciaPantalla()
    }
}