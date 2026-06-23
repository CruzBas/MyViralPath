package com.example.myviralpath.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.theme.*

@Composable
fun RegistrationScreen() {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        // Logo Placeholder (Matching the shape in the image)
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
                .background(Color.White, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            // Placeholder content for logo: Green top half
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFF8BAE9C), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Regístrate",
            color = TextoPrimario,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Comienza tu camino a la viralidad",
            color = TextoSecundario,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        CustomTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Nombre completo"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Correo electrónico"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Contraseña",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* TODO: Registrarse action */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Registrarse",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Divider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = BordeTxt,
                thickness = 1.dp
            )
            Text(
                text = " O CONTINÚA CON ",
                color = TextoSecundario,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = BordeTxt,
                thickness = 1.dp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        SocialButton(text = "Continuar con Apple")
        Spacer(modifier = Modifier.height(16.dp))
        SocialButton(text = "Continuar con Google")

        Spacer(modifier = Modifier.height(48.dp))

        Row {
            Text(text = "¿Ya tienes una cuenta? ", color = TextoSecundario, fontSize = 14.sp)
            Text(
                text = "Iniciar sesión",
                color = TextoPrimario,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextoSecundario, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundTxt,
            unfocusedContainerColor = BackgroundTxt,
            disabledContainerColor = BackgroundTxt,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = NaranjaPrimario,
            focusedTextColor = TextoPrimario,
            unfocusedTextColor = TextoPrimario
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}

@Composable
fun SocialButton(text: String) {
    Button(
        onClick = { /* TODO: Social login */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BackgroundTxt
        ),
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(TextoSecundario, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text.first().toString(),
                    color = BackgroundTxt,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = text,
                color = TextoPrimario,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // Just to keep the text centered
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    MyViralPathTheme {
        RegistrationScreen()
    }
}
