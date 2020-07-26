package messaging.utils

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kotlin.reflect.KProperty

data class ValidationException(val errors: List<ValidationError>) : RuntimeException()

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ValidationError.NotNull::class, name = "NotNull"),
    JsonSubTypes.Type(value = ValidationError.NotBlank::class, name = "NotBlank"),
    JsonSubTypes.Type(value = ValidationError.TooLong::class, name = "TooLong"),
    JsonSubTypes.Type(value = ValidationError.BelowMinimumValue::class, name = "BelowMinimumValue"),
    JsonSubTypes.Type(value = ValidationError.InvalidDate::class, name = "InvalidDate")
)
sealed class ValidationError {

    @JsonProperty("type")
    @Suppress("UNUSED")
    fun type(): String = this::class.simpleName!!

    data class NotNull(
        val field: String
    ) : ValidationError()

    data class NotBlank(
        val field: String
    ) : ValidationError()

    data class TooLong(
        val field: String,
        val maxLength: Int
    ) : ValidationError()

    data class BelowMinimumValue(
        val field: String,
        val minValue: Float
    ) : ValidationError()

    data class InvalidDate(
        val field: String,
        val description: String
    ) : ValidationError()

}

data class Validation<T>(
    val field: String,
    val errors: List<ValidationError>,
    val rawValue: T
) {
    val value: T
        get() = when (errors.isEmpty()) {
            true -> rawValue
            false -> throw ValidationException(errors = errors)
        }

    fun error(err: ValidationError): Validation<T> {
        return copy(errors = errors + err)
    }

    fun updateValue(newVal: T): Validation<T> {
        return copy(rawValue = newVal)
    }
}

fun <T> KProperty<T>.validate(): Validation<T> {
    return Validation(field = name, errors = emptyList(), rawValue = call())
}

fun <T : Any> Validation<T?>.notNull(): Validation<T> {
    @Suppress("UNCHECKED_CAST")
    return when (rawValue) {
        null -> error(ValidationError.NotNull(field)) as Validation<T>
        else -> this as Validation<T>
    }
}

fun <T : Int?> Validation<T>.min(min: Int): Validation<T> {
    return when {
        rawValue == null -> this
        rawValue < min -> error(ValidationError.BelowMinimumValue(field = field, minValue = min.toFloat()))
        else -> this
    }
}

fun <T : CharSequence?> Validation<T>.notBlank(): Validation<T> {
    @Suppress("UNCHECKED_CAST")
    return when {
        rawValue == null -> this
        rawValue.isBlank() -> error(ValidationError.NotBlank(field))
        else -> updateValue(rawValue.trim() as T)
    }
}

fun <T : CharSequence?> Validation<T>.maxLength(maxLength: Int): Validation<T> {
    @Suppress("UNCHECKED_CAST")
    return when {
        rawValue == null -> this
        rawValue.length > maxLength -> error(ValidationError.TooLong(field = field, maxLength = maxLength))
        else -> updateValue(rawValue.trim() as T)
    }
}

fun ensureValidations(vararg validations: Validation<*>) {
    val errors = validations.flatMap(Validation<*>::errors)
    if (errors.isNotEmpty()) {
        throw ValidationException(errors = errors)
    }
}

/******************************************
 * Generated with generate_validateAll.rb *
 *****************************************/
fun <A0, T> validateAll(arg0: Validation<A0>, block: (A0) -> T): T {
    ensureValidations(arg0)
    return block.invoke(arg0.value)
}

fun <A0, A1, T> validateAll(arg0: Validation<A0>, arg1: Validation<A1>, block: (A0, A1) -> T): T {
    ensureValidations(arg0, arg1)
    return block.invoke(arg0.value, arg1.value)
}

fun <A0, A1, A2, T> validateAll(
    arg0: Validation<A0>,
    arg1: Validation<A1>,
    arg2: Validation<A2>,
    block: (A0, A1, A2) -> T
): T {
    ensureValidations(arg0, arg1, arg2)
    return block.invoke(arg0.value, arg1.value, arg2.value)
}

fun <A0, A1, A2, A3, T> validateAll(
    arg0: Validation<A0>,
    arg1: Validation<A1>,
    arg2: Validation<A2>,
    arg3: Validation<A3>,
    block: (A0, A1, A2, A3) -> T
): T {
    ensureValidations(arg0, arg1, arg2, arg3)
    return block.invoke(arg0.value, arg1.value, arg2.value, arg3.value)
}

fun <A0, A1, A2, A3, A4, T> validateAll(
    arg0: Validation<A0>,
    arg1: Validation<A1>,
    arg2: Validation<A2>,
    arg3: Validation<A3>,
    arg4: Validation<A4>,
    block: (A0, A1, A2, A3, A4) -> T
): T {
    ensureValidations(arg0, arg1, arg2, arg3, arg4)
    return block.invoke(arg0.value, arg1.value, arg2.value, arg3.value, arg4.value)
}
