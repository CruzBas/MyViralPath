package com.example.myviralpath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.myviralpath.data.models.ContentIdea
import com.example.myviralpath.data.models.NextStep
import com.example.myviralpath.data.models.StrategicPlan
import com.example.myviralpath.service.PlanEstrategicoViewModel
import com.example.myviralpath.ui.theme.*
import java.util.*

@Composable
fun PlanEstrategicoScreen(
    viewModel: PlanEstrategicoViewModel,
    onNavigateToNewContent: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()
    val nextSteps by viewModel.nextSteps.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentPlan()
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = errorMessage!!,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = BackgroundOscuro,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (currentPlan != null && !isGenerating) {
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
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NaranjaPrimario)
                    }
                }
            } else if (isGenerating) {
                item {
                    GeneratingState()
                }
            } else if (currentPlan == null) {
                item {
                    EmptyPlanState(onGenerateClick = { viewModel.generateNewPlan() })
                }
            } else {
                item {
                    AIInsightsSection(currentPlan!!)
                    Spacer(Modifier.height(24.dp))
                    TasksSection(
                        tasks = nextSteps,
                        onTaskToggle = { taskId, isCompleted -> viewModel.toggleTaskCompletion(taskId, isCompleted) }
                    )
                    Spacer(Modifier.height(32.dp))
                    PublicationsSection(ideas = currentPlan!!.content_ideas)
                    Spacer(Modifier.height(100.dp)) // Space for FAB
                }
            }
        }
    }
}

@Composable
fun EmptyPlanState(onGenerateClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Rounded.AutoAwesome,
            contentDescription = null,
            tint = NaranjaPrimario,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No tienes un plan para hoy",
            color = TextoPrimario,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Deja que la IA analice tus métricas y te proponga una estrategia basada en tu nicho y audiencia.",
            color = TextoSecundario,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onGenerateClick,
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = TextoPrimario)
            Spacer(Modifier.width(8.dp))
            Text("Generar Plan con IA", color = TextoPrimario, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GeneratingState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = NaranjaPrimario)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Analizando tus métricas y nicho...",
            color = TextoSecundario,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "La IA está generando tu estrategia del día ✨",
            color = NaranjaPrimario,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AIInsightsSection(plan: StrategicPlan) {
    Surface(
        color = BackgroundTxt,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BordeTxt, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = NaranjaPrimario)
                Spacer(Modifier.width(8.dp))
                Text("Insights Estratégicos", color = TextoPrimario, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text("⏱️ Mejor hora hoy: ${plan.best_posting_time ?: "N/A"}", color = TextoSecundario, fontSize = 14.sp)
            Text("🎯 Plataforma foco: ${plan.recommended_platform ?: "N/A"}", color = TextoSecundario, fontSize = 14.sp)
            Text("📈 Potencial: ${plan.growth_potential ?: "N/A"}", color = TextoSecundario, fontSize = 14.sp)
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
    val weekNumber = "HOY" 

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
        
        val days = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE")
        val today = Calendar.getInstance()
        val startOfWeek = today.clone() as Calendar
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 5) {
                val dayCalendar = startOfWeek.clone() as Calendar
                dayCalendar.add(Calendar.DAY_OF_MONTH, i)
                val dayNum = dayCalendar.get(Calendar.DAY_OF_MONTH)
                val isSelected = dayNum == selectedDay
                
                CalendarDayItem(
                    dayName = days[i],
                    dayNumber = dayNum.toString(),
                    isSelected = isSelected,
                    onClick = { /* Only showing current week, click ignored for now */ }
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
fun TasksSection(tasks: List<NextStep>, onTaskToggle: (String, Boolean) -> Unit) {
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
        
        if (tasks.isEmpty()) {
            Text("No hay tareas asignadas.", color = TextoSecundario)
        } else {
            tasks.forEach { task ->
                TaskItem(
                    title = task.title, 
                    isCompleted = task.is_completed,
                    onToggle = { onTaskToggle(task.id, !task.is_completed) }
                )
            }
        }
    }
}

@Composable
fun TaskItem(title: String, isCompleted: Boolean, onToggle: () -> Unit) {
    Surface(
        color = BackgroundTxt,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, if (isCompleted) Color(0xFF4CAF50) else BordeTxt, RoundedCornerShape(24.dp))
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (isCompleted) Color(0xFF4CAF50) else TextoSecundario,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    color = if (isCompleted) TextoSecundario else TextoPrimario, 
                    fontSize = 14.sp,
                    textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                )
            }
        }
    }
}

@Composable
fun PublicationsSection(ideas: List<ContentIdea>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ideas de Publicaciones (IA)",
                color = TextoPrimario,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(16.dp))
        
        if (ideas.isEmpty()) {
            Text("No hay ideas generadas aún.", color = TextoSecundario)
        } else {
            ideas.forEach { idea ->
                PublicationCard(
                    type = idea.type.uppercase(),
                    title = idea.title,
                    platform = idea.platform,
                    time = idea.recommended_time,
                    status = idea.status.uppercase(),
                    icon = Icons.Default.Layers 
                )
            }
        }
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
