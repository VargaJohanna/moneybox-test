package com.example.minimoneybox.test.idling

import androidx.test.espresso.IdlingResource

interface FetcherListener : IdlingResource {
    fun doneFetching()
    fun beginFetching()
}