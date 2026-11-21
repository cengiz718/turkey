package com.example.turkeyguide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.turkeyguide.data.CATEGORIES
import com.example.turkeyguide.data.CITIES
import com.example.turkeyguide.data.Category
import com.example.turkeyguide.data.Place
import com.example.turkeyguide.data.PlacesApi
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GuideScreen(PlacesApi(BuildConfig.PLACES_KEY))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(api: PlacesApi) {
    var query by remember { mutableStateOf("") }
    var city by remember { mutableStateOf<String?>(null) }
    var category by remember { mutableStateOf<Category?>(null) }
    var results by remember { mutableStateOf<List<Place>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var cityMenu by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun runSearch() {
        val text = listOfNotNull(
            query.trim().takeIf { it.isNotEmpty() },
            category?.query,
            city
        ).joinToString(" ")

        if (text.isBlank()) return

        scope.launch {
            loading = true
            error = null
            try {
                results = api.search(text, category?.includedType)
            } catch (e: Exception) {
                error = e.message ?: "Bilinmeyen hata"
                results = emptyList()
            } finally {
                loading = false
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(12.dp)) {

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Ne arıyorsun? (örn: Kadıköy manzaralı)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                OutlinedButton(onClick = { cityMenu = true }) {
                    Text(city ?: "Şehir seç")
                }
                DropdownMenu(
                    expanded = cityMenu,
                    onDismissRequest = { cityMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Tüm Türkiye") },
                        onClick = {
                            city = null
                            cityMenu = false
                        }
                    )
                    CITIES.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c) },
                            onClick = {
                                city = c
                                cityMenu = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.padding(horizontal = 8.dp))
            Button(onClick = { runSearch() }) { Text("Ara") }
        }

        Row(
            Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CATEGORIES.forEach { c ->
                FilterChip(
                    selected = category == c,
                    onClick = { category = if (category == c) null else c },
                    label = { Text(c.label) }
                )
            }
        }

        if (loading) {
            CircularProgressIndicator(Modifier.padding(8.dp))
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(results) { p ->
                Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        p.photoName?.let { photo ->
                            AsyncImage(
                                model = api.photoUrl(photo),
                                contentDescription = p.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                        }
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        Text("⭐ ${p.rating?.toString() ?: "-"}  (${p.reviewCount} yorum)")
                        Text(p.address)
                        p.phone?.let { Text("📞 $it") }

                        val lat = p.lat
                        val lng = p.lng
                        if (lat != null && lng != null) {
                            TextButton(onClick = {
                                val uri = Uri.parse(
                                    "geo:$lat,$lng?q=$lat,$lng(${Uri.encode(p.name)})"
                                )
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            }) {
                                Text("Yol tarifi")
                            }
                        }
                    }
                }
            }
        }
    }
}
