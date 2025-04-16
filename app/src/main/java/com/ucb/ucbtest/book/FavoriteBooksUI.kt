package com.ucb.ucbtest.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ucb.domain.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteBooksUI(
    viewModel: BookViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    // Cargar los favoritos al entrar a la pantalla.
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    val bookState by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Libros Favoritos") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            when (val state = bookState) {
                is BookViewModel.BookState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is BookViewModel.BookState.Success -> {
                    if (state.books.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No tienes libros favoritos guardados")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Busca libros y márcalos como favoritos",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        // Usando LazyVerticalGrid similar al primer archivo para mantener la consistencia
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.books) { book ->
                                FavoriteBookGridItem(
                                    book = book,
                                    onFavoriteClick = { viewModel.toggleFavorite(book) }
                                )
                            }
                        }
                    }
                }
                is BookViewModel.BookState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // Estado inicial u otro imprevisto.
                    Text(
                        text = "Cargando favoritos...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteBookGridItem(book: Book, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box {
            Column {
                // Cover image
                if (book.cover_i != null) {
                    AsyncImage(
                        model = "https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg",
                        contentDescription = "Portada de ${book.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Sin imagen")
                    }
                }

                // Book details with light purple background
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F0F7))
                        .padding(8.dp)
                ) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (book.author_name.isNotEmpty()) {
                        Text(
                            text = book.author_name[0],
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.DarkGray
                        )
                    }

                    book.first_publish_year?.let {
                        Text(
                            text = "Año: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            // Botón de favorito (siempre visible y activo en esta pantalla)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onFavoriteClick() }
                )
            }
        }
    }
}