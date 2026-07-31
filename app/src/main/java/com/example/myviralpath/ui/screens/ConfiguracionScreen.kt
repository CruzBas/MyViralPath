package com.example.myviralpath.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.R
import com.example.myviralpath.ui.theme.BackgroundOscuro
import com.example.myviralpath.ui.theme.BackgroundTxt
import com.example.myviralpath.ui.theme.BordeTxt
import com.example.myviralpath.ui.theme.NaranjaPrimario
import com.example.myviralpath.ui.theme.TextoPrimario
import com.example.myviralpath.ui.theme.TextoSecundario

@Composable
fun ConfiguracionScreen(
    isLoading: Boolean,
    isInstagramLinked: Boolean,
    isYoutubeLinked: Boolean,
    onLinkInstagram: () -> Unit,
    onLinkYoutube: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .padding(24.dp)
    ) {
        Text(
            text = "Configuración",
            color = TextoPrimario,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // SECCIÓN: CONEXIONES SOCIALES
        Text(
            text = "Conexiones Sociales",
            color = TextoSecundario,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        SocialConnectionCard(
            title = "YouTube",
            iconRes = R.drawable.ic_launcher_foreground, // fallback icon since we don't have youtube specific one imported
            iconTint = Color(0xFFFF0000),
            isLinked = isYoutubeLinked,
            isLoading = isLoading,
            onLinkClick = onLinkYoutube
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        SocialConnectionCard(
            title = "Meta (Facebook / Instagram)",
            iconRes = R.drawable.ic_account_box, // fallback icon
            iconTint = Color(0xFF1877F2),
            isLinked = isInstagramLinked,
            isLoading = isLoading,
            onLinkClick = onLinkInstagram
        )

        Spacer(modifier = Modifier.height(48.dp))

        // SECCIÓN: CUENTA
        Text(
            text = "Cuenta",
            color = TextoSecundario,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
            Icon(Icons.AutoMirrored.Rounded.ExitToApp, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar sesión",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SocialConnectionCard(
    title: String,
    iconRes: Int,
    iconTint: Color,
    isLinked: Boolean,
    isLoading: Boolean,
    onLinkClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundTxt, RoundedCornerShape(16.dp))
            .border(1.dp, BordeTxt, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = TextoPrimario,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (isLinked) {
            Row(
                modifier = Modifier
                    .background(Color(0xFF4CAF50).copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Conectado",
                    color = Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Button(
                onClick = onLinkClick,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaPrimario),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = TextoPrimario,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Link,
                        contentDescription = null,
                        tint = TextoPrimario,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vincular",
                        color = TextoPrimario,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
