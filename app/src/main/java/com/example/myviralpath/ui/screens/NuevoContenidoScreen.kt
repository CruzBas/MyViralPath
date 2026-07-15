package com.example.myviralpath.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.components.LocalSnackbarHostState
import com.example.myviralpath.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoContenidoScreen(
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf("Instagram") }
    var selectedFormat by remember { mutableStateOf("Reel") }
    var selectedDate by remember { mutableStateOf("Hoy, 25 Oct") }
    var selectedTime by remember { mutableStateOf("") } // Empty means Draft

    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = BackgroundOscuro,
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Contenido", color = TextoPrimario) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = TextoPrimario)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundOscuro)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Título del contenido", color = TextoSecundario, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = BackgroundTxt,
                    focusedContainerColor = BackgroundTxt,
                    unfocusedBorderColor = BordeTxt,
                    focusedBorderColor = NaranjaPrimario,
                    cursorColor = NaranjaPrimario,
                    focusedTextColor = TextoPrimario,
                    unfocusedTextColor = TextoPrimario
                ),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ej: 3 Mitos sobre el Ayuno", color = TextoSecundario) }
            )

            Spacer(Modifier.height(24.dp))

            Text("¿Dónde lo vas a subir?", color = TextoSecundario, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            val apps = listOf("Instagram", "TikTok", "YouTube", "Threads")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(apps) { app ->
                    SelectableChip(
                        label = app,
                        isSelected = selectedApp == app,
                        onClick = { selectedApp = app }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Formato", color = TextoSecundario, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            val formats = listOf("Foto", "Video", "Reel", "Historia")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(formats) { format ->
                    SelectableChip(
                        label = format,
                        isSelected = selectedFormat == format,
                        onClick = { selectedFormat = format }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Programar publicación", color = TextoSecundario, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).clickable { /* Date Picker */ },
                    color = BackgroundTxt,
                    shape = RoundedCornerShape(12.dp),
                    border = borderStroke()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NaranjaPrimario, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(selectedDate, color = TextoPrimario, fontSize = 14.sp)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f).clickable { 
                        // Toggle between a time and empty (Draft) for demo
                        selectedTime = if (selectedTime.isEmpty()) "09:00" else ""
                    },
                    color = BackgroundTxt,
                    shape = RoundedCornerShape(12.dp),
                    border = borderStroke()
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = NaranjaPrimario, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedTime.isEmpty()) "Borrador" else selectedTime, color = TextoPrimario, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Contenido guardado correctamente")
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Guardar Contenido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, BordeTxt)

@Composable
fun SelectableChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (isSelected) NaranjaPrimario else BackgroundTxt,
        shape = RoundedCornerShape(100.dp),
        border = if (isSelected) null else borderStroke()
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else TextoPrimario,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview
@Composable
fun NuevoContenidoPreview() {
    MyViralPathTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
            NuevoContenidoScreen(onBack = {})
        }
    }
}
