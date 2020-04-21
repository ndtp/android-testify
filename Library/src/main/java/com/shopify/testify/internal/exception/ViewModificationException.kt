package com.shopify.testify.internal.exception

class ViewModificationException(throwable: Throwable) : Exception("""

* Test failed due to ${throwable::class.java.simpleName} in the ViewModifications

Caused by ${throwable::class.java.simpleName}: "${throwable.message}"

${throwable.stackTrace.joinToString(separator = "\n") { it.toString() }}

""")
