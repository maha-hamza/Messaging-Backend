package config

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import messaging.message.MessageController
import messaging.user.UserController

fun Routing.get(path: String, func: suspend (ApplicationCall) -> Unit) = get(path) { func(call) }
fun Routing.post(path: String, func: suspend (ApplicationCall) -> Unit) = post(path) { func(call) }

fun Routing.route() {

    val messageController = inject<MessageController>()
    val userController = inject<UserController>()

    post("/api/message", messageController::send)
    get("/api/message/sent/all", messageController::viewAllSentMessages)
    get("/api/message/sent/to-user", messageController::viewSentMessagesToParticularUser)

    get("/api/message/received/all", messageController::viewAllReceivedMessages)
    get("/api/message/received/from-user", messageController::viewReceivedMessagesFromUser)

    post("/api/user", userController::createUser)
    get("/api/user", userController::getAllUsers)
}