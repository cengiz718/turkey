package com.example.turkeyguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class GuidePlace(
    val name: String,
    val city: String,
    val category: String,
    val description: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TurkeyGuideScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurkeyGuideScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val hasApiKey = remember {
        BuildConfig.PLACES_KEY.isNotBlank() && !BuildConfig.PLACES_KEY.startsWith("AIza...buraya")
    }

    val samplePlaces = remember {
        listOf(
            GuidePlace("Nemrut Dağı", "Adıyaman", "Tarihi Alan", "Kommagene Krallığı'na ait dev heykeller ve gün doğumu manzarası."),
            GuidePlace("Ayasofya-i Kebir Cami-i Şerifi", "İstanbul", "Tarihi Yapı", "Tarihi yarımadada yer alan mimari ve kültürel miras."),
            GuidePlace("Göreme Açık Hava Müzesi", "Nevşehir", "Doğa & Tarih", "Kapadokya bölgesindeki kaya oyma kiliseler ve peribacaları."),
            GuidePlace("Efes Antik Kenti", "İzmir", "Antik Kent", "Celsus Kütüphanesi ve Antik Tiyatro ile ünlü antik liman kenti."),
            GuidePlace("Pamukkale Travertenleri", "Denizli", "Doğa Harikası", "Termal suların oluşturduğu beyaz kalsiyum terasları ve Hierapolis.")
        )
    }

    val filteredPlaces = remember(searchQuery) {
        if (searchQuery.isBlank()) samplePlaces
        else samplePlaces.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.city.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TurkeyGuide", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Şehir, mekan veya kategori ara...") },
                singleLine = true
            )

            AssistChip(
                onClick = {},
                label = {
                    Text(
                        if (hasApiKey) "Places API Anahtarı Aktif"
                        else "Çevrimdışı Rehber Modu (API Anahtarı Bekleniyor)"
                    )
                }
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPlaces) { place ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = place.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${place.city} • ${place.category}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = place.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
