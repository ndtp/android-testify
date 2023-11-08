package dev.testify.compose.exception

import dev.testify.core.exception.TestifyException

class ComposeContainerNotFoundException :
    TestifyException(
        tag = "NO_COMPOSE_CONTAINER",
        message =
        """

* Failed to find Compose content view. Please use ComposableTestActivity.
"""
    )
