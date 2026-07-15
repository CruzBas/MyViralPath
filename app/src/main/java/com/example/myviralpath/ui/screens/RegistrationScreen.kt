package com.example.myviralpath.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.R
import com.example.myviralpath.service.AuthState
import com.example.myviralpath.service.AuthViewModel
import com.example.myviralpath.ui.theme.*

@Composable
fun RegistrationScreen(
    viewModel: AuthViewModel,
    onLoginClick: () -> Unit
) {
    val authState by viewModel.authState

    RegistrationScreenContent(
        authState = authState,
        onRegistroClick = { email, password, nombre -> viewModel.signUp(email, password, nombre) },
        onLoginClick = onLoginClick
    )
}

@Composable
fun RegistrationScreenContent(
    authState: AuthState,
    onRegistroClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
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

            Image(
                painter = painterResource(id = R.drawable.ic_favorite),
                contentDescription = "Logo de ViralPath",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp)), 
                contentScale = ContentScale.Crop
            )


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


        when (authState) {
            is AuthState.Error -> {
                Text(authState.message, color = Color.Red, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            is AuthState.Success -> {
                Text(authState.message, color = Color.Green, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            else -> {}
        }

        Button(
            onClick = { onRegistroClick(email, password, nombre) },
            enabled = authState !is AuthState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
            } else {
                Text(
                    text = "Registrarse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


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
                color = NaranjaPrimario,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLoginClick() }
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
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextoSecundario, fontSize = 14.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NaranjaPrimario,
            unfocusedBorderColor = BordeTxt,
            focusedContainerColor = BackgroundTxt,
            unfocusedContainerColor = BackgroundTxt,
            focusedTextColor = TextoPrimario,
            unfocusedTextColor = TextoPrimario,
            cursorColor = NaranjaPrimario
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
                textAlign = TextAlign.Center
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
        RegistrationScreenContent(
            authState = AuthState.Idle,
            onLoginClick = { },
            onRegistroClick = { _, _, _ -> }
        )
    }
}
