package com.ar.backgroundlocation

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URLEncoder
import androidx.core.net.toUri


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
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enviar ubicación por WhatsApp",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF075E54)
        )

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Número de contacto") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true
        )

        TextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Mensaje personalizado") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = false,
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Servicio iniciado", Toast.LENGTH_SHORT).show()
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_SERVICE_START
                        context.startService(this)
                    }

                    val prefs = context.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
                    latitude = prefs.getString("last_lat", null)
                    longitude = prefs.getString("last_lng", null)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Iniciar servicio")
            }

            Button(
                onClick = {
                    Toast.makeText(context, "Servicio detenido", Toast.LENGTH_SHORT).show()
                    Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_SERVICE_STOP
                        context.startService(this)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB00020),
                    contentColor = Color.White
                )
            ) {
                Text("Detener servicio")
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Latitud: ${latitude ?: "no disponible"}")
            Text("Longitud: ${longitude ?: "no disponible"}")
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                val uri = "https://api.whatsapp.com/send?phone=${phoneNumber.trim()}&text=${
                    URLEncoder.encode(
                        fullMessage,
                        "UTF-8"
                    )
                }".toUri()

                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366),
                contentColor = Color.White
            )
        ) {
            Text("Enviar por WhatsApp")
        }
    }
}

