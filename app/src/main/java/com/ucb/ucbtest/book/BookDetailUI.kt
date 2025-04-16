package com.ucb.ucbtest.book

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ucb.domain.Book
import com.ucb.ucbtest.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailUI(book: Book, onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalles del Libro",
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = DarkPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cover image or placeholder
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (book.cover_i != null) {
                    AsyncImage(
                        model = "https://covers.openlibrary.org/b/id/${book.cover_i}-L.jpg",
                        contentDescription = "Book cover",
                        modifier = Modifier
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Placeholder for missing cover
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1E3A5F),
                                        Color(0xFF0D2137)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = book.title.first().toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Title with higher contrast
            Text(
                text = book.title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Authors
            if (book.author_name.isNotEmpty()) {
                Text(
                    text = "Autor(es):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkPrimary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = book.author_name.joinToString(", "),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Publication year
            book.first_publish_year?.let {
                Text(
                    text = "Año de publicación:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkPrimary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "$it",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Open Library Link with accented button
            val bookUrl = "https://openlibrary.org${book.key}"
            Button(
                onClick = { /* Aquí podrías abrir un navegador con el URL */ },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Ver en Open Library")
            }
        }
    }
}