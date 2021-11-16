package com.io.tazarapp.data.model

data class Paging<T> (
    val previous : Int?,
    val next : Int?,
    val count : Int?,
    val results : ArrayList<T>?
)