package com.example.myviralpath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.theme.BackgroundOscuro
import com.example.myviralpath.ui.theme.NaranjaPrimario
import com.example.myviralpath.ui.theme.TextoPrimario
import com.example.myviralpath.ui.theme.TextoSecundario

@Composable
fun VinculacionCuentasScreen(
    onLinkInstagram: () -> Unit,
    onLinkYoutube: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Vinculación Necesaria",
            color = TextoPrimario,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Para poder acceder al Dashboard Estratégico y ver tus analíticas, necesitas vincular al menos una de tus cuentas sociales.",
            color = TextoSecundario,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onLinkInstagram,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario)
        ) {
            Text(
                text = "Vincular Instagram",
                color = TextoPrimario,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onLinkYoutube,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario)
        ) {
            Text(
                text = "Vincular YouTube",
                color = TextoPrimario,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
