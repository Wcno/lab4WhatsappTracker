package com.ar.backgroundlocation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URLEncoder


@Composable
fun WhatsAppLocationScreen() {
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf("+507") }
    var message by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<String?>(null) }
    var longitude by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enviar ubicación por WhatsApp",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Ingrese el # de contacto") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Ingrese su mensaje") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Información recopilada", Toast.LENGTH_SHORT).show()

                // Iniciar el servicio
                Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_SERVICE_START
                    context.startService(this)
                }

                // Leer SharedPreferences
                val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
                latitude = prefs.getString("last_lat", null)
                longitude = prefs.getString("last_lng", null)
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Text("Start Service")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Latitud: ${latitude ?: "no disponible"}")
        Text("Longitud: ${longitude ?: "no disponible"}")

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            Toast.makeText(context, "Service Stop button clicked", Toast.LENGTH_SHORT).show()

            Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_SERVICE_STOP
                context.startService(this)
            }
        }) {
            Text("Stop Service")
        }
        Button(
            onClick = {
                if (phoneNumber.isBlank()) {
                    Toast.makeText(context, "Número inválido", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val locationText = if (latitude != null && longitude != null) {
                    "\nUbicación: https://maps.google.com/?q=$latitude,$longitude"
                } else {
                    ""
                }

                val fullMessage = message + locationText

                val uri = Uri.parse(
                    String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                        phoneNumber.trim(),
                        URLEncoder.encode(fullMessage, "UTF-8")
                    )
                )

                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366), // Verde estilo WhatsApp
                contentColor = Color.White
            )
        ) {
            Text("Enviar por WhatsApp")
        }

    }
}
