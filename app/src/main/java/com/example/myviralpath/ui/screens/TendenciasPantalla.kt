package com.example.myviralpath.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.R
import com.example.myviralpath.ui.theme.*

@Composable
fun TendenciasPantalla() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 96.dp)
        ) {
            Encabezado()

            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Tendencias",
                    color = TextoPrimario,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Análisis en tiempo real para Fitness",
                    color = TextoSecundario,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                FiltrosPlataformas()

                Spacer(modifier = Modifier.height(32.dp))

                TarjetaTendencia(
                    estado = "EXPLOTANDO",
                    porcentaje = "94%",
                    titulo = "HIIT Challenge",
                    colorEstado = Color(0xFF42E47A),
                    tipoGrafica = TipoGrafica.LINEA
                )

                Spacer(modifier = Modifier.height(20.dp))

                TarjetaTendencia(
                    estado = "ESTABLE",
                    porcentaje = "82%",
                    titulo = "Meal Prep Hacks",
                    colorEstado = Color(0xFF8CCEFF),
                    tipoGrafica = TipoGrafica.DISCONTINUA
                )

                Spacer(modifier = Modifier.height(20.dp))

                TarjetaTendencia(
                    estado = "EN ASCENSO",
                    porcentaje = "76%",
                    titulo = "Minimalist Gym Wear",
                    colorEstado = Color(0xFF42E47A),
                    tipoGrafica = TipoGrafica.BARRAS
                )

                Spacer(modifier = Modifier.height(40.dp))

                TituloSeccion(
                    icono = "↗",
                    titulo = "ANÁLISIS DE TENDENCIA"
                )

                Spacer(modifier = Modifier.height(16.dp))

                TarjetaAnalisis()

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Audios Tendencia",
                    color = TextoSecundario,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                AudioTendencia(
                    titulo = "Rising Phonk (Viral Edit)",
                    uso = "Used in 1.2M videos"
                )

                Spacer(modifier = Modifier.height(14.dp))

                AudioTendencia(
                    titulo = "Gym Motivation 2024",
                    uso = "Used in 840K videos"
                )

                Spacer(modifier = Modifier.height(14.dp))

                AudioTendencia(
                    titulo = "Chill Lofi Meal Prep",
                    uso = "Used in 450K videos"
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun Encabezado() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "ViralPath",
            color = TextoPrimario,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { },
            modifier = Modifier
                .size(40.dp)
                .background(BackgroundTxt, CircleShape)
        ) {
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
}

@Composable
private fun FiltrosPlataformas() {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ChipPlataforma(
            texto = "Instagram",
            seleccionado = true
        )

        ChipPlataforma(
            texto = "TikTok",
            seleccionado = false
        )

        ChipPlataforma(
            texto = "YouTube",
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
                color = if (seleccionado) NaranjaPrimario else BackgroundTxt,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = if (seleccionado) Color.Transparent else BordeTxt,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = texto,
            color = if (seleccionado) Color.White else TextoSecundario,
            fontSize = 12.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
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
            .background(
                color = BackgroundTxt,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = BordeTxt,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = colorEstado.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = estado,
                    color = colorEstado,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = porcentaje,
                color = TextoPrimario,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = titulo,
            color = TextoPrimario,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Viral Potential Index",
            color = TextoSecundario,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.height(80.dp)) {
            when (tipoGrafica) {
                TipoGrafica.LINEA -> GraficaLinea()
                TipoGrafica.DISCONTINUA -> GraficaDiscontinua()
                TipoGrafica.BARRAS -> GraficaBarras()
            }
        }
    }
}

@Composable
private fun GraficaLinea() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.8f)
            cubicTo(
                size.width * 0.3f, size.height * 0.7f,
                size.width * 0.6f, size.height * 0.2f,
                size.width, size.height * 0.1f
            )
        }

        drawPath(
            path = path,
            color = NaranjaPrimario,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

@Composable
private fun GraficaDiscontinua() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        drawLine(
            color = Color(0xFF8CCEFF),
            start = Offset(0f, size.height * 0.6f),
            end = Offset(size.width * 0.4f, size.height * 0.5f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )

        drawLine(
            color = Color(0xFF8CCEFF),
            start = Offset(size.width * 0.6f, size.height * 0.4f),
            end = Offset(size.width, size.height * 0.3f),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun GraficaBarras() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val count = 6
        val spacing = 8.dp.toPx()
        val itemWidth = (size.width - (spacing * (count - 1))) / count
        val heights = listOf(0.3f, 0.5f, 0.4f, 0.7f, 0.6f, 0.9f)

        heights.forEachIndexed { index, h ->
            drawRect(
                color = Color(0xFF42E47A).copy(alpha = 0.3f + (h * 0.7f)),
                topLeft = Offset(index * (itemWidth + spacing), size.height * (1f - h)),
                size = Size(itemWidth, size.height * h)
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
            text = titulo,
            color = TextoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun TarjetaAnalisis() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = BackgroundTxt,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                1.dp,
                BordeTxt,
                RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            FilaAnalisis(
                icono = "↪",
                titulo = "El gancho",
                descripcion = "Primeros 3 segundos con corte rápido para maximizar retención.",
                colorIcono = NaranjaPrimario
            )

            Spacer(modifier = Modifier.height(20.dp))

            FilaAnalisis(
                icono = "▮",
                titulo = "Audio: 'Rising Phonk'",
                descripcion = "Trending +450% en las últimas 24 horas.",
                colorIcono = Color(0xFF42E47A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NaranjaPrimario
                )
            ) {
                Text(
                    text = "Aplicar Estrategia Sugerida",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BordeTxt.copy(alpha = 0.3f))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = BackgroundTxt.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        1.dp,
                        BordeTxt,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "✦",
                    color = NaranjaPrimario,
                    fontSize = 42.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "IA ha detectado una anomalía de crecimiento en audios similares de HIIT.",
                    color = TextoPrimario,
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
                .size(32.dp)
                .background(
                    colorIcono.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icono,
                color = colorIcono,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titulo,
                color = TextoPrimario,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = descripcion,
                color = TextoSecundario,
                fontSize = 13.sp,
                lineHeight = 18.sp
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
                color = BackgroundTxt,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                BordeTxt,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BordeTxt, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▷",
                color = TextoPrimario,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                color = TextoPrimario,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = uso,
                color = TextoSecundario,
                fontSize = 12.sp
            )
        }

        Button(
            onClick = { },
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NaranjaPrimario
            )
        ) {
            Text(
                text = "Usar",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
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