/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 ndtp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.testify.annotation

import androidx.test.platform.app.InstrumentationRegistry

/**
 * Returns the fully qualified dot-separated name of the annotation required by the Gradle plugin.
 */
fun getScreenshotAnnotationName(): String =
    InstrumentationRegistry.getArguments().getString("annotation", ScreenshotInstrumentation::class.qualifiedName)

/**
 * Get the [ScreenshotInstrumentation] instance associated with the test method
 *
 * @param classAnnotations - A [List] of all the [Annotation]s defined on the currently running test class
 * @param methodAnnotations - A [Collection] of all the [Annotation]s defined on the currently running test method
 */
fun getScreenshotInstrumentationAnnotation(
    classAnnotations: List<Annotation>,
    methodAnnotations: Collection<Annotation>?
): Annotation? {
    val annotationName = getScreenshotAnnotationName()
    return classAnnotations.findAnnotation(annotationName) ?: methodAnnotations?.findAnnotation(annotationName)
}
