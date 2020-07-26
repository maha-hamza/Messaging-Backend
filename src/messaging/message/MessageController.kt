package messaging.message

import config.body
import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.http.httpDateFormat
import io.ktor.response.respond
import org.koin.core.KoinComponent
import org.koin.core.inject

class MessageController : KoinComponent {

    private val messageService by inject<MessageService>()

    suspend fun send(call: ApplicationCall) {
        val fromUser = call.request.headers["from-user"]
        val toUser = call.request.headers["to-user"]
        val message = call.body<NewMessage>()
        call.respond(
            HttpStatusCode.Created,
            messageService.send(
                message = message.validate(),
                from = fromUser,
                to = toUser
            )
        )
    }

    suspend fun viewAllSentMessages(call: ApplicationCall) {
        val user = call.request.headers["user"]

        call.respond(
            HttpStatusCode.OK,
            messageService.viewAllSentMessages(
                user = user
            )
        )
    }

    suspend fun viewSentMessagesToParticularUser(call: ApplicationCall) {
        val user = call.request.headers["user"]
        val toUser = call.request.headers["to-user"]

        call.respond(
            HttpStatusCode.OK,
            messageService.viewSentMessagesToParticularUser(
                user = user,
                to = toUser
            )
        )
    }

    suspend fun viewAllReceivedMessages(call: ApplicationCall) {
        val user = call.request.headers["user"]

        call.respond(
            HttpStatusCode.OK,
            messageService.viewAllReceivedMessages(
                user = user
            )
        )
    }

    suspend fun viewReceivedMessagesFromUser(call: ApplicationCall) {
        val user = call.request.headers["user"]
        val toUser = call.request.headers["from-user"]

        call.respond(
            HttpStatusCode.OK,
            messageService.viewReceivedMessagesFromUser(
                user = user,
                from = toUser
            )
        )
    }
}