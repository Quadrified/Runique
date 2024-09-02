package com.quadrified.core.domain.util

// D, E => The out keyword signifies that D is a covariant type parameter. This means that D can be used as an output type (e.g., return type) but not as an input type (e.g., function argument). Covariance allows a Result object to be used in contexts where a Result with a more specific type parameter D is expected.
sealed interface Result<out D, out E : Error> {
    data class Success<out D>(val data: D) : Result<D, Nothing>

    data class Error<out E : com.quadrified.core.domain.util.Error>(val error: E) :
        Result<Nothing, E>

}

// Extension function to Result<D, E> that maps the result
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map {}
}

// typealias allows to create an alias for an existing type, makes code more readable and easier to work with
// Here used as a variance of Result for empty data
typealias EmptyResult<E> = Result<Unit, E>

