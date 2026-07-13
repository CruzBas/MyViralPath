package com.example.myviralpath.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.theme.BackgroundTxt
import com.example.myviralpath.ui.theme.BordeTxt
import com.example.myviralpath.ui.theme.NaranjaPrimario
import com.example.myviralpath.ui.theme.TextoPrimario
val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState no fue proporcionado")
}

@Composable
fun CustomSnackbar(snackbarData: SnackbarData) {
    Card(
        shape = RoundedCornerShape(100.dp),
        colors = CardDefaults.cardColors(Color.Blue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .padding(16.dp)
            .height(56.dp)
            .fillMaxWidth()
            .border(1.dp, BordeTxt, RoundedCornerShape(100.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = NaranjaPrimario,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = snackbarData.visuals.message,
                color = TextoPrimario,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}
