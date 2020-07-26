package messaging.user

import config.body
import config.respondNullable
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserController : KoinComponent {

    private val userService by inject<UserService>()

    suspend fun createUser(call: ApplicationCall) {
        val user = call.body<NewUser>()

        call.respond(
            HttpStatusCode.Created,
            userService.createUser(
                user = user.validate()
            )
        )
    }

    suspend fun getAllUsers(call: ApplicationCall) {

        call.respond(
            HttpStatusCode.OK,
            userService.getAllUsers()
        )
    }
}