package com.example.minimoneybox.idling

import androidx.test.espresso.IdlingResource

interface FetcherListener : IdlingResource {
    fun doneFetching()
    fun beginFetching()
}