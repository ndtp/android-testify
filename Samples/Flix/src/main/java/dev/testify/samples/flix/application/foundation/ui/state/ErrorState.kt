package dev.testify.samples.flix.application.foundation.ui.state

sealed class ErrorState(
    val message: String
)

class WarningErrorState(
    message: String
) : ErrorState(message)

