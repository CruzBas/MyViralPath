package com.example.myviralpath.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myviralpath.ui.AuthState
import com.example.myviralpath.ui.AuthViewModel
import com.example.myviralpath.ui.theme.*
import com.example.myviralpath.R 

@Composable
fun PantallaLogin(
    viewModel: AuthViewModel,
    onRegistroClick: () -> Unit
) {
    val authState by viewModel.authState
    
    PantallaLoginContent(
        authState = authState,
        onLoginClick = { correo, contra -> viewModel.signIn(correo, contra) },
        onRegistroClick = onRegistroClick
    )
}

@Composable
fun PantallaLoginContent(
    authState: AuthState,
    onLoginClick: (String, String) -> Unit,
    onRegistroClick: () -> Unit
) {
    // Estados para almacenar el texto ingresado por el usuario
    var correo by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }

    //Estados para ver/ocultar la contraseña
    var contraVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundOscuro)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(60.dp))

        //Logo
        Image(
            painter = painterResource(id = R.drawable.ic_favorite),
            contentDescription = "Logo de ViralPath",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp)), //Bordes redondeados
            contentScale = ContentScale.Crop // Se encarga de que la imagen llene el espacio sin distorsionarse
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ViralPath",
            color = TextoPrimario,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Iniciar Sesión",
            color = TextoPrimario,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bienvenido",
            color = TextoSecundario,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(32.dp))

        //Campo de correo
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Correo electrónico",
                color = TextoPrimario,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                placeholder = { Text("ejemplo@gmail.com", color = TextoSecundario)},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaranjaPrimario,
                    unfocusedBorderColor = BordeTxt,
                    focusedContainerColor = BackgroundTxt,
                    unfocusedContainerColor = BackgroundTxt,
                    focusedTextColor = TextoPrimario,
                    unfocusedTextColor = TextoPrimario,
                    cursorColor = NaranjaPrimario
                )
            )
        }

        //Campo de contra
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Contraseña",
                color = TextoPrimario,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = contra,
                onValueChange = { contra = it },
                placeholder = { Text("*******", color = TextoSecundario)},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                visualTransformation = if (contraVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    //Texto clikeable para Ver/Ocultar contra
                    val textoOV = if (contraVisible) "Ocultar" else "Ver"
                    Text(
                        text = textoOV,
                        color = TextoSecundario,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable{ contraVisible = !contraVisible }
                            .padding(end = 12.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaranjaPrimario,
                    unfocusedBorderColor = BordeTxt,
                    focusedContainerColor = BackgroundTxt,
                    unfocusedContainerColor = BackgroundTxt,
                    focusedTextColor = TextoPrimario,
                    unfocusedTextColor = TextoPrimario,
                    cursorColor = NaranjaPrimario
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        //Olvidaste tu contraseña
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = NaranjaPrimario,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { /* Falta */}
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Mostrar errores o mensajes de éxito
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

        // Boton Principal (Conexion con Supabase)
        Button(
            onClick = { onLoginClick(correo, contra) },
            enabled = authState !is AuthState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
            } else {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) //Empuja la zona de registro hacia el fondo

        //Navegacion al registro
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = TextoSecundario)) {
                append("¿No tienes una cuenta?")
            }
            withStyle(style = SpanStyle(color = NaranjaPrimario, fontWeight = FontWeight.Bold)) {
                append("Registrate")
            }
        }

        Text(
            text = annotatedString,
            modifier = Modifier
                .padding(bottom = 40.dp)
                .clickable{ onRegistroClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaLoginPreview() {
    MyViralPathTheme {
        PantallaLoginContent(
            authState = AuthState.Idle,
            onLoginClick = { _, _ -> },
            onRegistroClick = { }
        )
    }
}

