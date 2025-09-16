package com.example.ebook.read.model

import androidx.compose.runtime.Immutable

@Immutable
data class Reader(
    val id: Long,
    val name: String,
    val imageUrl: String,

    val tagline: String = "",
    val tags: Set<String> = emptySet()
)


val snacks = listOf(
    Reader(
        id = 1L,
        name = "Cupcake",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",

    )
)