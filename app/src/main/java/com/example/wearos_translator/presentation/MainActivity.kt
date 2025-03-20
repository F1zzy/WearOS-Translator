package com.example.wearos_translator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Minimalistic WearOS Live Translator UI
 *
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TranslatorApp() }
    }

    @Composable
    fun TranslatorApp() {
        val scale = remember { Animatable(1f) }
        val coroutineScope = rememberCoroutineScope()
        var isListening by remember { mutableStateOf(false) }
        var showSettings by remember { mutableStateOf(false) }

        if (showSettings) {
            SettingsScreen { showSettings = false }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black), // Black background
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(300.dp) // Large circle button
                        .scale(scale.value) // Animated scaling effect
                        .clip(CircleShape)
                        .background(Color.Red)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    coroutineScope.launch {
                                        isListening = true
                                        scale.animateTo(0.85f, animationSpec = tween(100)) // Smooth shrink
                                        tryAwaitRelease()
                                        scale.animateTo(1f, animationSpec = tween(150)) // Smooth expand back
                                        isListening = false
                                        // TODO: Implement speech recognition
                                    }
                                },
                                onDoubleTap = {
                                    showSettings = true // Double tap opens settings
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isListening) {
                        Text("Listening...", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsScreen(onBack: () -> Unit) {
        var selectedInputLanguage by remember { mutableStateOf("English") }
        var selectedOutputLanguage by remember { mutableStateOf("French") }
        var enableAutoDetect by remember { mutableStateOf(true) }
        val languages = listOf("English", "French", "Spanish", "German", "Chinese", "Japanese", "Italian")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -50) {
                            onBack() // Swipe left to exit settings
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Input Language", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            LanguageDropdownMenu(languages, selectedInputLanguage) { selectedInputLanguage = it }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Output Language", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            LanguageDropdownMenu(languages, selectedOutputLanguage) { selectedOutputLanguage = it }
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Auto-detect Input Language", fontSize = 16.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = enableAutoDetect, onCheckedChange = { enableAutoDetect = it })
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onBack() }) {
                Text("Back", fontSize = 18.sp)
            }
        }
    }

    @Composable
    fun LanguageDropdownMenu(options: List<String>, selected: String, onSelected: (String) -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        Box {
            Button(onClick = { expanded = true }) {
                Text(selected)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang) },
                        onClick = {
                            onSelected(lang)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
