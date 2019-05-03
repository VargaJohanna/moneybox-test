package com.example.minimoneybox.test.idling

import androidx.test.espresso.IdlingResource


class FetcherListnerImpl: FetcherListener {
    private var idle = true
    private var resourceCallback:
            IdlingResource.ResourceCallback? = null

    override fun getName(): String {
        return FetcherListnerImpl::class.java.simpleName
    }

    override fun isIdleNow() = idle

    override fun registerIdleTransitionCallback(
        callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }

    override fun doneFetching() {
        idle = true
        resourceCallback?.onTransitionToIdle()
    }

    override fun beginFetching() {
        idle = false
    }
}