package dev.testify.internal

inline fun <reified T : Annotation> Collection<Annotation>.getAnnotation(): T? {
    return this.find { it is T } as? T
}
