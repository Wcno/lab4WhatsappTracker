package com.ar.backgroundlocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.net.URLEncoder
import androidx.core.net.toUri

@Composable
fun WhatsAppLocationScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

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
        Text("Enviar ubicación por WhatsApp", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Ingrese el # de contacto") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Ingrese su mensaje") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            textStyle = TextStyle(fontSize = 16.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val permission = Manifest.permission.ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val locationRequest = LocationRequest.create().apply {
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    numUpdates = 1
                    interval = 0
                }

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val loc = result.lastLocation ?: return
                        latitude = loc.latitude.toString()
                        longitude = loc.longitude.toString()
                        Toast.makeText(context, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            Text("Obtener ubicación")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Latitud: ${latitude ?: "no disponible"}")
        Text("Longitud: ${longitude ?: "no disponible"}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (phoneNumber.isBlank()) {
                    Toast.makeText(context, "Número inválido", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (latitude == null || longitude == null) {
                    Toast.makeText(context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val locationText = "\nUbicación: https://maps.google.com/?q=$latitude,$longitude"
                val fullMessage = message + locationText

                val uri = String.format(
                    "https://api.whatsapp.com/send?phone=%s&text=%s",
                    phoneNumber.trim(),
                    URLEncoder.encode(fullMessage, "UTF-8")
                ).toUri()

                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366),
                contentColor = Color.White
            )
        ) {
            Text("Enviar por WhatsApp")
        }
    }
}
