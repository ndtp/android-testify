package dev.testify.core.exception

class ViewModificationException(throwable: Throwable) : TestifyException(
    "VIEW_MODIFICATION",
    """

* Test failed due to ${throwable::class.java.simpleName} in the ViewModifications

Caused by ${throwable::class.java.simpleName}: "${throwable.message}"

${throwable.stackTrace.joinToString(separator = "\n") { it.toString() }}

"""
)
