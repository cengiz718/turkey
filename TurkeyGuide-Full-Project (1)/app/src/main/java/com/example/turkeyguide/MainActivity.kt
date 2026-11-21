package com.example.turkeyguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class GuidePlace(
    val id: String,
    val name: String,
    val city: String,
    val category: String,
    val description: String,
    val tip: String
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
    var selectedFilter by remember { mutableStateOf("Tümü") }
    var onlyFavorites by remember { mutableStateOf(false) }
    var favoriteIds by remember { mutableStateOf(setOf("nemrut")) }
    var selectedPlace by remember { mutableStateOf<GuidePlace?>(null) }

    val hasApiKey = remember {
        BuildConfig.PLACES_KEY.isNotBlank() && !BuildConfig.PLACES_KEY.startsWith("AIza...buraya")
    }

    val places = remember {
        listOf(
            GuidePlace(
                id = "nemrut",
                name = "Nemrut Dağı",
                city = "Adıyaman",
                category = "Tarihi Alan",
                description = "Kommagene Krallığı'na ait dev heykeller, tümülüs ve gün doğumu/gün batımı manzarasıyla UNESCO Dünya Mirası listesinde yer alır.",
                tip = "Gün doğumu saatlerinde rüzgar ve serinliğe karşı kalın kıyafet bulundurun."
            ),
            GuidePlace(
                id = "cendere",
                name = "Cendere Köprüsü",
                city = "Adıyaman",
                category = "Antik Yapı",
                description = "Roma İmparatoru Septimius Severus döneminde inşa edilen, tek kemerli görkemli antik taş köprü.",
                tip = "Yakınındaki Karakuş Tümülüsü ve Arsemia Antik Kenti ile aynı güzergahta gezilebilir."
            ),
            GuidePlace(
                id = "perre",
                name = "Perre Antik Kenti",
                city = "Adıyaman",
                category = "Antik Kent",
                description = "Kommagene'nin beş büyük kentinden biri olan Perre; kaya mezarları ve antik su kaynaklarıyla öne çıkar.",
                tip = "Sabah veya ikindi saatlerinde ışık antik dokuyu daha belirgin gösterir."
            ),
            GuidePlace(
                id = "ayasofya",
                name = "Ayasofya-i Kebir Cami-i Şerifi",
                city = "İstanbul",
                category = "Tarihi Yapı",
                description = "Tarihi Yarımada'da yer alan, devasa kubbesi ve zengin mimari geçmişiyle öne çıkan anıtsal eser.",
                tip = "Ziyaret saatlerini ve kalabalık yoğunluğunu günün erken saatlerine göre planlayın."
            ),
            GuidePlace(
                id = "goreme",
                name = "Göreme Açık Hava Müzesi",
                city = "Nevşehir",
                category = "Doğa & Tarih",
                description = "Kapadokya bölgesindeki kaya oyma kiliseleri, freskleri ve peribacalarıyla ünlü açık hava müzesi.",
                tip = "MüzeKart geçerliliği ve sıcak hava balonu saatleri için erken çıkış yapın."
            ),
            GuidePlace(
                id = "efes",
                name = "Efes Antik Kenti",
                city = "İzmir",
                category = "Antik Kent",
                description = "Celsus Kütüphanesi, Büyük Tiyatro ve mermer caddeleriyle dünyanın en iyi korunmuş antik kentlerinden biridir.",
                tip = "Zemin taşları için rahat tabanlı ayakkabı ve güneş koruması tercih edin."
            ),
            GuidePlace(
                id = "pamukkale",
                name = "Pamukkale Travertenleri",
                city = "Denizli",
                category = "Doğa Harikası",
                description = "Termal mineralli suların oluşturduğu beyaz kalsiyum terasları ve Hierapolis Antik Kenti ile ünlüdür.",
                tip = "Traverten alanına girişte ayakkabılarınızı çıkarmanız gerekir."
            )
        )
    }

    val filterChips = remember {
        listOf("Tümü", "Adıyaman", "İstanbul", "Nevşehir", "İzmir", "Denizli", "Tarihi Alan", "Antik Kent", "Doğa Harikası")
    }

    val filteredPlaces = remember(searchQuery, selectedFilter, onlyFavorites, favoriteIds) {
        places.filter { place ->
            val matchesFav = !onlyFavorites || favoriteIds.contains(place.id)
            val matchesChip = when (selectedFilter) {
                "Tümü" -> true
                else -> place.city.equals(selectedFilter, ignoreCase = true) ||
                    place.category.equals(selectedFilter, ignoreCase = true)
            }
            val matchesSearch = searchQuery.isBlank() ||
                place.name.contains(searchQuery, ignoreCase = true) ||
                place.city.contains(searchQuery, ignoreCase = true) ||
                place.category.contains(searchQuery, ignoreCase = true) ||
                place.description.contains(searchQuery, ignoreCase = true)

            matchesFav && matchesChip && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TurkeyGuide", fontWeight = FontWeight.Bold) },
                actions = {
                    FilterChip(
                        selected = onlyFavorites,
                        onClick = { onlyFavorites = !onlyFavorites },
                        label = { Text("Favoriler (${favoriteIds.size})") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Şehir, mekan veya kategori ara...") },
                singleLine = true
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterChips) { chip ->
                    FilterChip(
                        selected = selectedFilter == chip,
                        onClick = { selectedFilter = chip },
                        label = { Text(chip) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (hasApiKey) "Places API Aktif"
                            else "Çevrimdışı Rehber (${filteredPlaces.size} mekan)"
                        )
                    }
                )

                if (searchQuery.isNotBlank() || selectedFilter != "Tümü" || onlyFavorites) {
                    TextButton(
                        onClick = {
                            searchQuery = ""
                            selectedFilter = "Tümü"
                            onlyFavorites = false
                        }
                    ) {
                        Text("Filtreleri Sıfırla")
                    }
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredPlaces, key = { it.id }) { place ->
                    val isFav = favoriteIds.contains(place.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlace = place },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = place.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        favoriteIds = if (isFav) favoriteIds - place.id else favoriteIds + place.id
                                    }
                                ) {
                                    Text(if (isFav) "★ Favori" else "☆ Kaydet")
                                }
                            }
                            Text(
                                text = "${place.city} • ${place.category}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = place.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        }
    }

    selectedPlace?.let { place ->
        val isFav = favoriteIds.contains(place.id)
        ModalBottomSheet(
            onDismissRequest = { selectedPlace = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${place.city} • ${place.category}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = place.description,
                    style = MaterialTheme.typography.bodyLarge
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = "Gezi İpucu: ${place.tip}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            favoriteIds = if (isFav) favoriteIds - place.id else favoriteIds + place.id
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isFav) "Favorilerden Çıkar" else "Favorilere Ekle")
                    }
                    OutlinedButton(
                        onClick = { selectedPlace = null },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kapat")
                    }
                }
            }
        }
    }
}
