package com.shopify.testify.sample

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource

fun Any.registerIdlingRegistry() {
    registerIdlingRegistry(hashCode().toString())
}

fun Any.releaseIdlingRegistry() {
    releaseIdlingRegistry(hashCode().toString())
}

fun registerIdlingRegistry(key: String) {
    CountingIdlingResource(key).apply {
        increment()
        IdlingRegistry.getInstance().register(this)
    }
}

fun releaseIdlingRegistry(key: String) {
    IdlingRegistry.getInstance().resources.find { it.name == key }?.let { idlingResource ->
        (idlingResource as CountingIdlingResource).decrement()
        IdlingRegistry.getInstance().unregister(idlingResource)
    }
}
