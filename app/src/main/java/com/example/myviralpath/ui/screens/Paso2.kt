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
import com.example.myviralpath.ui.theme.*

// Pantalla de Onboarding Paso audiencia ideal

@Composable
fun AudienciaPantalla(
    onBackClick: () -> Unit = {},
    onFinish: (countryName: String, ageMin: Int, ageMax: Int, gender: String) -> Unit = { _, _, _, _ -> }
) {
    var countryName by remember { mutableStateOf("") }
    var ageRange by remember { mutableStateOf(18f..35f) }
    var selectedGender by remember { mutableStateOf("MIXTO") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
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

        Text(
            text = "Ahora, hablemos de tu audiencia ideal",
            color = TextoPrimario,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "ViralPath detectará tendencias basadas en este perfil para optimizar tu alcance.",
            color = TextoSecundario,
            fontSize = 16.sp,
            lineHeight = 22.sp
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = TextoSecundario,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Estamos construyendo tu perfil estratégico",
                color = TextoSecundario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
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
                tint = TextoSecundario,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = currentStep,
            color = NaranjaPrimario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

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
        color = NaranjaPrimario,
        trackColor = BackgroundTxt,
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextoSecundario,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

// Campo de búsqueda de país
@Composable
private fun BuscarPaisField(texto: String, onTextoChange: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        placeholder = { Text(text = "Buscar país...", color = TextoSecundario, fontSize = 14.sp) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar", tint = TextoSecundario)
        },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NaranjaPrimario,
            unfocusedBorderColor = BordeTxt,
            focusedContainerColor = BackgroundTxt,
            unfocusedContainerColor = BackgroundTxt,
            focusedTextColor = TextoPrimario,
            unfocusedTextColor = TextoPrimario,
            cursorColor = NaranjaPrimario
        )
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
                color = if (selected) NaranjaPrimario
                else BordeTxt,
                shape = RoundedCornerShape(28.dp)
            )
            .background(BackgroundTxt, RoundedCornerShape(28.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = bandera, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = nombre,
            color = if (selected) TextoPrimario
            else TextoSecundario,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionLabel(text = "RANGO DE EDAD")
        Text(
            text = "${rangoEdad.start.toInt()} — ${rangoEdad.endInclusive.toInt()} años",
            color = NaranjaPrimario,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }

    RangeSlider(
        value = rangoEdad,
        onValueChange = onRangoEdadChange,
        valueRange = 13f..65f,
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = NaranjaPrimario,
            inactiveTrackColor = BordeTxt
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("13", "25", "40", "55", "65+").forEach { marca ->
            Text(text = marca, color = TextoSecundario, fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
                color = if (selected) NaranjaPrimario
                else BordeTxt,
                shape = RoundedCornerShape(24.dp)
            )
            .background(BackgroundTxt, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) NaranjaPrimario
            else TextoSecundario,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = if (selected) TextoPrimario
            else TextoSecundario,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun ContinuarBoton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Text(text = "Finalizar Configuración", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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