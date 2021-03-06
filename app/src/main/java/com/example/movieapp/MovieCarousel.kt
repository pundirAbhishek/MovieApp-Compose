package com.example.movieapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

const val posterAspectRatio = .675f

val verticalGradient = Brush.verticalGradient(
    0f to Color.Transparent,
    0.3f to Color.White,
    1f to Color.White
)

@Composable
fun MovieScreen() {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val posterWidthDp = screenWidth * 0.6f
    val posterSpacingPx = with(density) { posterWidthDp.toPx() + 20.dp.toPx() }

    var offset by remember { mutableStateOf(0f) }
    val indexFraction = -1 * offset / posterSpacingPx

    val scrollableState = rememberScrollableState {
        offset += it
        when {
            offset > 0 -> {
                offset = 0f
                0f
            }
            offset <= ((-1 * posterSpacingPx) * (movies.size-1)) -> {
                offset = ((-1 * posterSpacingPx) * (movies.size-1))
                0f
            }
            else -> {
                it
            }
        }
    }

    Box(
        Modifier
            .background(Color.Black)
            .fillMaxSize()
            .scrollable(
                scrollableState,
                Orientation.Horizontal
            )
    ) {
        movies.forEachIndexed { index, movie ->
            val isInRange = (index >= indexFraction - 1 && indexFraction + 1 > index)
            val opacity = if (isInRange) 1f else 0f
            val shape = when {
                !isInRange -> RectangleShape
                // 0, 0.25 -> 0.25, 1f
                // 1, 0.25 -> 0f, 0.25
                index <= indexFraction -> {
                    val fraction = indexFraction - index
                    FractionalRectangleShape(fraction.coerceIn(0f, 1f-Float.MIN_VALUE), 1f)
                }
                else -> {
                    val fraction = indexFraction - index + 1
                    FractionalRectangleShape(0f, fraction.coerceIn(Float.MIN_VALUE, 1f))
                }
            }
            Image(
                painterResource(id = movie.imageResourceId),
                contentDescription = "${movie.title} movie poster",
                modifier = Modifier
                    .alpha(opacity)
                    .clip(shape = shape)
                    .fillMaxWidth()
                    .aspectRatio(posterAspectRatio),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .background(verticalGradient)
        )
        movies.forEachIndexed { index, movie ->
            val center = posterSpacingPx * index
            val distFromCenter = abs(offset + center) / posterSpacingPx
            MoviePoster(
                movie = movie,
                modifier = Modifier
                    .offset(getX = { center + offset }, getY = { lerp(0f, 100f, distFromCenter) })
                    .width(posterWidthDp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun MoviePoster(movie: Movie, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(id = movie.imageResourceId),
            contentDescription = "${movie.title} movie poster",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(posterAspectRatio)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = movie.title,
            fontSize = 24.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            for (chip in movie.chips) {
                Chip(chip)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        BuyTicketButton(onClick = {})
    }
}

@Composable
fun BuyTicketButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        modifier = Modifier
            .padding(vertical = 10.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)

    ) {
        Text(
            "Buy Ticket",
            fontSize = 20.sp,
            color = Color.White
        )
    }
}

@Composable
fun Chip(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        fontSize = 10.sp,
        color = Color.Gray,
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(50))
            .padding(vertical = 3.dp, horizontal = 8.dp)

    )
}
