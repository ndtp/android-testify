package com.andrewcarmichael.flix.application.foundation.ui.state

sealed class ErrorState(
    val message: String
)

class WarningErrorState(
    message: String
) : ErrorState(message)

