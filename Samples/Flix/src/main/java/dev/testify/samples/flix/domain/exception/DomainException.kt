package dev.testify.samples.flix.domain.exception

open class DomainException(throwable: Throwable) : Throwable(throwable) {
    constructor(message: String) : this(Throwable(message))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
}

class NetworkUnavailableException(throwable: Throwable) : DomainException(throwable) {
    constructor(errorMessage: String) : this(Throwable(errorMessage))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
}

class RemoteApiFailedException(throwable: Throwable) : DomainException(throwable) {
    constructor(errorMessage: String) : this(Throwable(errorMessage))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
}

class UnknownDomainException(throwable: Throwable) : DomainException(throwable) {
    constructor(errorMessage: String) : this(Throwable(errorMessage))
    constructor(message: String, throwable: Throwable) : this(Exception(message, throwable))
}
