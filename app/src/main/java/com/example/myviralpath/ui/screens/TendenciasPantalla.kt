package com.example.myviralpath.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.theme.MyViralPathTheme

private val FondoPrincipal = Color(0xFF06182A)
private val FondoSuperior = Color(0xFF071526)
private val FondoTarjeta = Color(0xFF102238)
private val FondoTarjetaClaro = Color(0xFF182A40)
private val BordeTarjeta = Color(0xFF1C324A)
private val Naranja = Color(0xFFFF7415)
private val Durazno = Color(0xFFFFAE87)
private val Verde = Color(0xFF42E47A)
private val AzulClaro = Color(0xFF8CCEFF)
private val TextoPrincipal = Color(0xFFD6E2F5)
private val TextoSecundario = Color(0xFFE0C2B8)
private val FondoChip = Color(0xFF1B2D41)

@Composable
fun TendenciasPantalla() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPrincipal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 96.dp)
        ) {
            Encabezado()

            Column(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Tendencias",
                    color = TextoPrincipal,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Análisis en tiempo real para Fitness",
                    color = TextoSecundario,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                FiltrosPlataformas()

                Spacer(modifier = Modifier.height(64.dp))

                TarjetaTendencia(
                    estado = "EXPLOTANDO",
                    porcentaje = "94%",
                    titulo = "HIIT Challenge",
                    colorEstado = Verde,
                    tipoGrafica = TipoGrafica.LINEA
                )

                Spacer(modifier = Modifier.height(20.dp))

                TarjetaTendencia(
                    estado = "ESTABLE",
                    porcentaje = "82%",
                    titulo = "Meal Prep Hacks",
                    colorEstado = AzulClaro,
                    tipoGrafica = TipoGrafica.DISCONTINUA
                )

                Spacer(modifier = Modifier.height(20.dp))

                TarjetaTendencia(
                    estado = "EN ASCENSO",
                    porcentaje = "76%",
                    titulo = "Minimalist Gym Wear",
                    colorEstado = Verde,
                    tipoGrafica = TipoGrafica.BARRAS
                )

                Spacer(modifier = Modifier.height(72.dp))

                TituloSeccion(
                    icono = "↗",
                    titulo = "Análisis de Tendencia"
                )

                Spacer(modifier = Modifier.height(28.dp))

                TarjetaAnalisis()

                Spacer(modifier = Modifier.height(52.dp))

                Text(
                    text = "Audios Tendencia",
                    color = TextoPrincipal,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AudioTendencia(
                    titulo = "Rising Phonk\n(Viral Edit)",
                    uso = "Used in 1.2M videos"
                )

                Spacer(modifier = Modifier.height(14.dp))

                AudioTendencia(
                    titulo = "Gym Motivation\n2024",
                    uso = "Used in 840K\nvideos"
                )

                Spacer(modifier = Modifier.height(14.dp))

                AudioTendencia(
                    titulo = "Chill Lofi Meal\nPrep",
                    uso = "Used in 450K\nvideos"
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        BarraInferior(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun Encabezado() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoSuperior)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF20334A), CircleShape)
                .border(1.dp, Naranja, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "VP",
                color = Durazno,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "ViralPath",
            color = Durazno,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "◉",
            color = AzulClaro,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.width(22.dp))

        IconButton(onClick = { }) {
            Text(
                text = "♧",
                color = Durazno,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
private fun FiltrosPlataformas() {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ChipPlataforma(
            texto = "▣  Instagram",
            seleccionado = true
        )

        ChipPlataforma(
            texto = "▰  TikTok",
            seleccionado = false
        )

        ChipPlataforma(
            texto = "▣  YouTube",
            seleccionado = false
        )
    }
}

@Composable
private fun ChipPlataforma(
    texto: String,
    seleccionado: Boolean
) {
    Box(
        modifier = Modifier
            .background(
                color = if (seleccionado) Naranja else FondoChip,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 20.dp, vertical = 11.dp)
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Color(0xFF3A1C0D) else TextoSecundario,
            fontSize = 12.sp,
            fontWeight = if (seleccionado) FontWeight.Medium else FontWeight.Normal
        )
    }
}

private enum class TipoGrafica {
    LINEA,
    DISCONTINUA,
    BARRAS
}

@Composable
private fun TarjetaTendencia(
    estado: String,
    porcentaje: String,
    titulo: String,
    colorEstado: Color,
    tipoGrafica: TipoGrafica
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(298.dp)
            .background(
                color = FondoTarjeta,
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                width = 1.dp,
                color = BordeTarjeta,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(28.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = colorEstado.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = estado,
                    color = colorEstado,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = porcentaje,
                color = when (porcentaje) {
                    "94%" -> Durazno
                    "76%" -> Verde
                    else -> TextoPrincipal
                },
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = titulo,
            color = TextoPrincipal,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Viral Potential Index",
            color = TextoSecundario,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        when (tipoGrafica) {
            TipoGrafica.LINEA -> GraficaLinea()
            TipoGrafica.DISCONTINUA -> GraficaDiscontinua()
            TipoGrafica.BARRAS -> GraficaBarras()
        }
    }
}

@Composable
private fun GraficaLinea() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
    ) {
        val path = Path().apply {
            moveTo(size.width * 0.08f, size.height * 0.82f)
            cubicTo(
                size.width * 0.30f,
                size.height * 0.68f,
                size.width * 0.42f,
                size.height * 0.43f,
                size.width * 0.62f,
                size.height * 0.18f
            )
        }

        drawPath(
            path = path,
            color = Durazno,
            style = Stroke(
                width = 6.dp.toPx(),
                cap = StrokeCap.Square
            )
        )

        val sombra = Path().apply {
            moveTo(size.width * 0.62f, size.height * 0.18f)
            lineTo(size.width * 0.90f, size.height * 0.18f)
            lineTo(size.width * 0.90f, size.height * 0.90f)
            lineTo(size.width * 0.62f, size.height * 0.90f)
            close()
        }

        drawPath(
            path = sombra,
            color = Durazno.copy(alpha = 0.12f)
        )
    }
}

@Composable
private fun GraficaDiscontinua() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
    ) {
        drawLine(
            color = AzulClaro,
            start = Offset(size.width * 0.12f, size.height * 0.65f),
            end = Offset(size.width * 0.34f, size.height * 0.61f),
            strokeWidth = 6.dp.toPx()
        )

        drawLine(
            color = AzulClaro,
            start = Offset(size.width * 0.56f, size.height * 0.69f),
            end = Offset(size.width * 0.78f, size.height * 0.74f),
            strokeWidth = 6.dp.toPx()
        )
    }
}

@Composable
private fun GraficaBarras() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val ancho = size.width / 6.5f
        val espacio = 6.dp.toPx()
        val alturas = listOf(0.28f, 0.42f, 0.56f, 0.70f, 0.86f)

        alturas.forEachIndexed { index, altura ->
            drawRect(
                color = Verde.copy(alpha = 0.22f + index * 0.15f),
                topLeft = Offset(
                    x = index * (ancho + espacio),
                    y = size.height * (1f - altura)
                ),
                size = Size(
                    width = ancho,
                    height = size.height * altura
                )
            )
        }
    }
}

@Composable
private fun TituloSeccion(
    icono: String,
    titulo: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icono,
            color = Durazno,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = titulo,
            color = TextoPrincipal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun TarjetaAnalisis() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = FondoTarjeta,
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                1.dp,
                BordeTarjeta,
                RoundedCornerShape(28.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(28.dp)
        ) {
            FilaAnalisis(
                icono = "↪",
                titulo = "El gancho",
                descripcion = "Primeros 3 segundos con\ncorte rápido para maximizar\nretención.",
                colorIcono = Durazno
            )

            Spacer(modifier = Modifier.height(24.dp))

            FilaAnalisis(
                icono = "▮",
                titulo = "Audio: 'Rising Phonk'",
                descripcion = "Trending +450% en las\núltimas 24 horas.",
                colorIcono = Verde
            )

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Durazno,
                    contentColor = Color(0xFF572715)
                )
            ) {
                Text(
                    text = "Aplicar Estrategia Sugerida",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoTarjetaClaro)
                .padding(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FondoTarjeta.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(
                        1.dp,
                        BordeTarjeta,
                        RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✦",
                    color = Durazno,
                    fontSize = 42.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "IA ha detectado una anomalía de\ncrecimiento en audios similares\nde HIIT.",
                    color = TextoPrincipal,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FilaAnalisis(
    icono: String,
    titulo: String,
    descripcion: String,
    colorIcono: Color
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    colorIcono.copy(alpha = 0.12f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icono,
                color = colorIcono,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = titulo,
                color = TextoPrincipal,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = descripcion,
                color = TextoSecundario,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun AudioTendencia(
    titulo: String,
    uso: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = FondoTarjeta,
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                1.dp,
                BordeTarjeta,
                RoundedCornerShape(28.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color(0xFF243A51), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▷",
                color = TextoPrincipal,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                color = TextoPrincipal,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = uso,
                color = TextoSecundario,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }

        Button(
            onClick = { },
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Naranja,
                contentColor = Color(0xFF43200D)
            )
        ) {
            Text(
                text = "Usar en\nEstrategia",
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BarraInferior(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(FondoTarjeta)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemNavegacion("▦", "Dashboard", false)
        ItemNavegacion("↗", "Tendencias", true)
        ItemNavegacion("▤", "Plan", false)
        ItemNavegacion("♙", "Perfil", false)
    }
}

@Composable
private fun ItemNavegacion(
    icono: String,
    texto: String,
    seleccionado: Boolean
) {
    Column(
        modifier = Modifier
            .background(
                color = if (seleccionado) Naranja else Color.Transparent,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(
                horizontal = if (seleccionado) 18.dp else 8.dp,
                vertical = 6.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icono,
            color = if (seleccionado) Color(0xFF3A1C0D) else TextoSecundario,
            fontSize = 18.sp
        )

        Text(
            text = texto,
            color = if (seleccionado) Color(0xFF3A1C0D) else TextoPrincipal,
            fontSize = 10.sp
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun TendenciasPantallaPreview() {
    MyViralPathTheme {
        TendenciasPantalla()
    }
}