package com.ucb.ucbtest.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ucb.domain.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookUI(viewModel: BookViewModel = hiltViewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val bookState by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    var showFavoritesOnly by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Búsqueda de Libros") },
                actions = {
                    // Botón para mostrar solo favoritos
                    IconButton(onClick = {
                        showFavoritesOnly = !showFavoritesOnly
                        if (showFavoritesOnly) {
                            viewModel.loadFavorites()
                        } else if (searchQuery.isNotBlank()) {
                            viewModel.searchBooks(searchQuery)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = if (showFavoritesOnly) "Mostrar búsqueda" else "Mostrar favoritos"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar libros") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        showFavoritesOnly = false
                        viewModel.searchBooks(searchQuery)
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    showFavoritesOnly = false
                    viewModel.searchBooks(searchQuery)
                }),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estado de la búsqueda
            when (val state = bookState) {
                is BookViewModel.BookState.Initial -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Ingrese un término para buscar libros o vea sus favoritos")
                    }
                }
                is BookViewModel.BookState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BookViewModel.BookState.Success -> {
                    // Mostrar el título apropiado
                    Text(
                        text = if (showFavoritesOnly) "Mis Libros Favoritos" else "Resultados de la búsqueda",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Usar LazyVerticalGrid en lugar de LazyColumn para mostrar 3 elementos por fila
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.books) { book ->
                            BookGridItem(
                                book = book,
                                isFavorite = state.favoriteKeys.contains(book.key),
                                onFavoriteClick = { viewModel.toggleFavorite(book) }
                            )
                        }
                    }
                }
                is BookViewModel.BookState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.message)
                    }
                }
            }
        }
    }
}

@Composable
fun BookGridItem(book: Book, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp) // Hacemos la tarjeta más alta
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen más alta
            Box(
                modifier = Modifier
                    .height(140.dp) // Aumentado
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (book.cover_i != null) {
                    AsyncImage(
                        model = "https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg",
                        contentDescription = "Book cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "No Image",
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Título
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Autor
            if (book.author_name.isNotEmpty()) {
                Text(
                    text = book.author_name.first(),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            // Año de publicación
            book.first_publish_year?.let {
                Text(
                    text = "Año: $it",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botón favorito
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Eliminar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}

// Mantener el diseño anterior por si es necesario
@Composable
fun BookItem(book: Book, isFavorite: Boolean, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Cover image
            if (book.cover_i != null) {
                AsyncImage(
                    model = "https://covers.openlibrary.org/b/id/${book.cover_i}-M.jpg",
                    contentDescription = "Book cover",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image")
                }
            }

            // Book details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (book.author_name.isNotEmpty()) {
                    Text(
                        text = book.author_name.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                book.first_publish_year?.let {
                    Text(
                        text = "Año: $it",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Botón de favorito
            IconButton(
                onClick = onFavoriteClick
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Eliminar de favoritos" else "Añadir a favoritos",
                    tint = if (isFavorite) Color.Red else Color.Gray
                )
            }
        }
    }
}