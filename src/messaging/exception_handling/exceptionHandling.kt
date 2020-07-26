package messaging.exception_handling

import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import messaging.message.*
import messaging.user.UserAlreadyExistedException
import messaging.utils.ValidationException

val exceptionHandling: StatusPages.Configuration.() -> Unit = {

    exception<ValidationException> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            cause.errors
        )
    }

    exception<BodyDeserializationException> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            cause.message!!
        )
    }

    exception<UserAlreadyExistedException> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            cause.message!!
        )
    }

    exception<UserIdException> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            cause.message!!
        )
    }

    exception<UserInputForMessageSendingException> { cause ->
        call.respond(
            HttpStatusCode.BadRequest,
            cause.message!!
        )
    }

    exception<UserNotFoundException> { cause ->
        call.respond(
            HttpStatusCode.NotFound,
            cause.message!!
        )
    }
}