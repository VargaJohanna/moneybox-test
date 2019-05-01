package com.example.minimoneybox.ext

import android.view.View

fun View.display(displayed: Boolean) {
    visibility = if (displayed) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.show(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}