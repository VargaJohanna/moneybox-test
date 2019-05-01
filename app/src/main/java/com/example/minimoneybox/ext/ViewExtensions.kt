package com.example.minimoneybox.ext

import android.view.View

fun View.display(displayed: Boolean) {
    visibility = if (displayed) {
        View.VISIBLE
    } else {
        View.GONE
    }
}