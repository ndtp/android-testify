package dev.testify.samples.paparazzi.ui.common.util

interface ImagePromise {
    fun resolve(): Any
}

internal class PathBasedImagePromise(
    private val path: String
) : ImagePromise {
    override fun resolve(): Any {
        return path
    }
}

fun imagePromise(path: String): ImagePromise {
    return PathBasedImagePromise(path)
}
