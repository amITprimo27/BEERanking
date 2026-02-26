package com.example.beeranking.model

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithUser(
    @Embedded val post: Post,
    @Relation(
        parentColumn = "postedBy",
        entityColumn = "id"
    )
    val user: User?
)