package com.example.ebook.read.model
import androidx.compose.runtime.Immutable



@Immutable
data class Bookstore(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val writer:String,
    val tagline: String = "",
    val tags: Set<String> = emptySet()
)


val bookstore = listOf(
    Bookstore(
        id = 1L,
        name = "Cupcake",
        tagline = "A tag line",
        imageUrl = "https://source.unsplash.com/pGM4sjt_BdQ",
        writer =  "yi",
        )
)