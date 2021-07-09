package helloktor.plugins.validation

import helloktor.models.TaskEditDto
import kotlinx.serialization.Serializable
import org.valiktor.ConstraintViolationException
import org.valiktor.functions.isNotBlank
import org.valiktor.i18n.mapToMessage
import java.util.*

fun TaskEditDto.validate() = try {
    org.valiktor.validate(this) {
        validate(TaskEditDto::name).isNotBlank()
    }
    ValidationResult(true, emptyList())
} catch (ex: ConstraintViolationException) {
    val errors = ex.constraintViolations
        .groupBy { it.property }
        .map {e ->
            ValidationError(
                e.key,
                e.value.firstOrNull()?.value?.toString() ?: "",
                e.value.mapToMessage(baseName = "messages", locale = Locale.ENGLISH).map { it.message }
            )
        }

    ValidationResult(false, errors)
}

@Serializable
data class ValidationResult(val isValid: Boolean, val errors: List<ValidationError>)
@Serializable
data class ValidationError(val name: String, val value: String, val messages: List<String>)



