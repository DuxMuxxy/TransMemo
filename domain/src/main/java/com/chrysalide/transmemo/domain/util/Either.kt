package com.chrysalide.transmemo.domain.util

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 *
 * Convention dictates that [Left] is used for "failure" and [Right] is used for "success".
 *
 * @param L The type of the left value.
 * @param R The type of the right value.
 */
sealed class Either<out L, out R> {
    /** * Represents the left side of [Either], which by convention is a "failure". */
    data class Left<out L>(
        val value: L
    ) : Either<L, Nothing>()

    /** * Represents the right side of [Either], which by convention is a "success". */
    data class Right<out R>(
        val value: R
    ) : Either<Nothing, R>()

    /**
     * Returns true if this is a Left, false otherwise.
     */
    fun isLeft() = this is Left<L>

    /**
     * Returns true if this is a Right, false otherwise.
     */
    fun isRight() = this is Right<R>

    /**
     * Applies the corresponding function to the value of the Either.
     *
     * @param leftFn The function to apply if this is a Left.
     * @param rightFn The function to apply if this is a Right.
     * @return The result of applying the function.
     */
    inline fun <T> fold(
        leftFn: (L) -> T,
        rightFn: (R) -> T
    ): T =
        when (this) {
            is Left -> leftFn(value)
            is Right -> rightFn(value)
        }

    /**
     * Applies the given function if this is a Right, otherwise returns the Left.
     *
     * @param fn The function to apply if this is a Right.
     * @return The result of applying the function, or the original Left.
     */
    inline fun <RR> map(fn: (R) -> RR): Either<L, RR> =
        when (this) {
            is Left -> this
            is Right -> Right(fn(value))
        }

    /**
     * Applies the given function if this is a Left, otherwise returns the Right.
     *
     * @param fn The function to apply if this is a Left.
     * @return The result of applying the function, or the original Right.
     */
    inline fun <LL> mapLeft(fn: (L) -> LL): Either<LL, R> =
        when (this) {
            is Left -> Left(fn(value))
            is Right -> this
        }
}

/**
 * Creates a Left instance of Either.
 *
 * @param value The value to wrap in a Left.
 * @return A Left instance of Either.
 */
fun <L> left(value: L): Either<L, Nothing> = Either.Left(value)

/**
 * Creates a Right instance of Either.
 *
 * @param value The value to wrap in a Right.
 * @return A Right instance of Either.
 */
fun <R> right(value: R): Either<Nothing, R> = Either.Right(value)
