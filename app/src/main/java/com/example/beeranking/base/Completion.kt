package com.example.beeranking.base

import com.example.beeranking.model.Post
import com.example.beeranking.model.User

typealias Completion = () -> Unit
typealias PostCompletion = (Post) -> Unit
typealias UserCompletion = (User) -> Unit
typealias StringCompletion = (String?) -> Unit


