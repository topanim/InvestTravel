package app.what.investtravel.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.what.foundation.ui.Gap
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.ktor.client.plugins.logging.Logging

@Composable
fun PlaceCard(banner: String, title: String, type: String, isChacked: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .height(200.dp)
    ) {

        SubcomposeAsyncImage(
            model = banner,
            modifier = Modifier.fillMaxSize(),
            loading = {
                CircularProgressIndicator()
            },
            contentDescription = "",

        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.fillMaxHeight(0.5f))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChacked,
                            onCheckedChange = {
                            })

                        Column {
                            Text(title, color = Color.White)
                            Gap(10)
                            Text(type, color = Color.White)
                        }

                    }
                }
            }
        }
    }

}

@Preview()
@Composable
fun PlaceCardPreview() {
    PlaceCard(
        banner = "https://mir-rem.ru/wp-content/uploads/2022/07/remont-tualeta-3-350x350.jpg",
        title = "Туалет",
        type = "Писуары",
        isChacked = false
    )
}