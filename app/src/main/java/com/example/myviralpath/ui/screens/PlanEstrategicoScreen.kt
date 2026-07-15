package com.example.myviralpath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.theme.*
import java.util.*
import java.util.Calendar

@Composable
fun PlanEstrategicoScreen(
    onNavigateToNewContent: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundOscuro,
        floatingActionButton = {
            Button(
                onClick = onNavigateToNewContent,
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Nuevo Contenido", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                HeaderSection()
                Spacer(Modifier.height(24.dp))
                CalendarSection()
                Spacer(Modifier.height(32.dp))
                TasksSection()
                Spacer(Modifier.height(32.dp))
                PublicationsSection()
                Spacer(Modifier.height(100.dp)) // Space for FAB
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = NaranjaPrimario,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Plan Estratégico",
                color = TextoPrimario,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = "Profile",
            tint = TextoSecundario,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun CalendarSection() {
    val calendar = Calendar.getInstance()
    val sdfMonth = java.text.SimpleDateFormat("MMMM yyyy", Locale("es"))
    val monthYear = sdfMonth.format(calendar.time)
    val weekNumber = "SEMANA 42" // Mocked as in screenshot

    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = monthYear.replaceFirstChar { it.uppercase() },
                color = TextoPrimario,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = weekNumber,
                color = TextoSecundario,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
        Spacer(Modifier.height(16.dp))
        
        // Simple Horizontal Weekly Calendar
        val days = listOf("LUN", "MAÑ", "MIÉ", "JUE", "VIE")
        val today = Calendar.getInstance()
        val startOfWeek = today.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 5) { // LUN to VIE as in image
                val dayCalendar = startOfWeek.clone() as Calendar
                dayCalendar.add(Calendar.DAY_OF_MONTH, i)
                val dayNum = dayCalendar.get(Calendar.DAY_OF_MONTH)
                val isSelected = dayNum == selectedDay
                
                CalendarDayItem(
                    dayName = days[i],
                    dayNumber = dayNum.toString(),
                    isSelected = isSelected,
                    onClick = { selectedDay = dayNum }
                )
            }
        }
    }
}

@Composable
fun CalendarDayItem(
    dayName: String,
    dayNumber: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) NaranjaPrimario else BackgroundTxt)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .width(40.dp)
    ) {
        Text(
            text = dayName,
            color = if (isSelected) Color.Black else TextoSecundario,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = dayNumber,
            color = if (isSelected) Color.Black else TextoPrimario,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
fun TasksSection() {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = NaranjaPrimario)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Tareas del día (IA)",
                color = TextoPrimario,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        
        TaskItem("Grabar gancho para Reel de HIIT", true)
        TaskItem("Responder comentarios en post de ayer", false)
        TaskItem("Investigar nuevo audio tendencia: Phonk", false, isTrending = true)
    }
}

@Composable
fun TaskItem(title: String, hasPriority: Boolean, isTrending: Boolean = false) {
    Surface(
        color = BackgroundTxt,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, BordeTxt, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = TextoSecundario,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextoPrimario, fontSize = 14.sp)
            }
            if (hasPriority) {
                Surface(
                    color = Color(0xFF1E3A24),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Prioridad",
                        color = Color(0xFF4CAF50),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            if (isTrending) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun PublicationsSection() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Próximas Publicaciones",
                color = TextoPrimario,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "VER TODO",
                color = NaranjaPrimario,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        
        PublicationCard(
            type = "REEL",
            title = "3 Mitos sobre el Ayuno Intermitente",
            platform = "Instagram",
            time = "18:00",
            status = "PROGRAMADO",
            icon = Icons.Default.Layers // Placeholder for Reel icon
        )
        
        PublicationCard(
            type = "CAROUSEL",
            title = "Guía Visual de Suplementos 2024",
            platform = "TikTok",
            time = "Mañana, 09:00",
            status = "BORRADOR",
            icon = Icons.Default.Layers // Placeholder for Carousel icon
        )
    }
}

@Composable
fun PublicationCard(
    type: String,
    title: String,
    platform: String,
    time: String,
    status: String,
    icon: ImageVector
) {
    Surface(
        color = BackgroundTxt,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, BordeTxt, RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BackgroundOscuro),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TextoSecundario)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = type, color = Color(0xFF4CAF50), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Rounded.AccountCircle, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = platform, color = TextoSecundario, fontSize = 10.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(text = title, color = TextoPrimario, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (status == "BORRADOR") Icons.Default.Add else Icons.Default.Schedule, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(text = time, color = TextoSecundario, fontSize = 12.sp)
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = BordeTxt,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = status,
                            color = TextoSecundario,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PlanEstrategicoPreview() {
    MyViralPathTheme {
        PlanEstrategicoScreen(onNavigateToNewContent = {})
    }
}
