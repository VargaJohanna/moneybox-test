package com.example.minimoneybox.ext

import android.app.Activity

/**
 * At the moment this is the simplest way to log out the user when the token has expired
 */
fun Activity.restart() {
    finish()
    startActivity(intent)
}