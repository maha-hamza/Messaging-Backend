package messaging.message

import abstracts.*
import abstracts.ProjectionAssertions.Companion.assertThat
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import messaging.user.User
import org.junit.jupiter.api.Test
import java.time.Instant

internal class MessageControllerTest : AbstractDBTest() {

    @Test
    fun `should send message successfully`() {

        val from = insertUser("Maha")
        val friend = insertUser("Friend")

        handle(
            uri = "/api/message",
            method = HttpMethod.Post,
            body = NewMessage(
                text = "Hello Friend"
            ),
            headers = mapOf(
                "from-user" to from.id,
                "to-user" to friend.id
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.Created)
                .contentType(JsonUtf8)
                .body<Message>()
                .isEqualToIgnoringGivenFields(
                    Message(
                        id = "",
                        text = "Hello Friend",
                        createdAt = Instant.now(),
                        fromUser = from.id,
                        toUser = friend.id
                    ),
                    User::createdAt.name,
                    User::id.name
                )
        }
    }

    @Test
    fun `should not view all sent messages if userid is blank `() {

        handle(
            uri = "/api/message/sent/all",
            method = HttpMethod.Get,
            headers = mapOf(
                "user" to ""
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.BadRequest)
                .contentType(PlainUtf8)
                .body<String>()
                .isEqualTo("SENDER id can't be null or blank")

        }
    }

    @Test
    fun `should view all sent messages`() {

        val from = insertUser("Maha")
        val friend = insertUser("Friend")

        val msg = insertMessage("Hello", from.id, friend.id)

        handle(
            uri = "/api/message/sent/all",
            method = HttpMethod.Get,
            headers = mapOf(
                "user" to from.id
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.OK)
                .contentType(JsonUtf8)
                .listBody<UserMessage>()
                .isEqualTo(
                    listOf(
                        UserMessage(
                            id = msg.id,
                            text = "Hello",
                            createdAt = msg.createdAt,
                            fromUserNickName = "Maha",
                            toUserNickName = "Friend"
                        )
                    )
                )
        }
    }

    @Test
    fun `should view all received messages`() {

        val user = insertUser("Maha")
        val friend = insertUser("Friend")

        val msg = insertMessage("Hello", friend.id, user.id)

        handle(
            uri = "/api/message/received/all",
            method = HttpMethod.Get,
            headers = mapOf(
                "user" to user.id
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.OK)
                .contentType(JsonUtf8)
                .listBody<UserMessage>()
                .isEqualTo(
                    listOf(
                        UserMessage(
                            id = msg.id,
                            text = "Hello",
                            createdAt = msg.createdAt,
                            fromUserNickName = "Friend",
                            toUserNickName = "Maha"
                        )
                    )
                )
        }
    }

    @Test
    fun `should view received messages from particular user`() {

        val user = insertUser("Maha")
        val friend = insertUser("Friend")
        val friend2 = insertUser("Friend2")

        val msg = insertMessage("Hello", friend.id, user.id)
        insertMessage("Hello", friend2.id, user.id)

        handle(
            uri = "/api/message/received/from-user",
            method = HttpMethod.Get,
            headers = mapOf(
                "user" to user.id,
                "from-user" to friend.id
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.OK)
                .contentType(JsonUtf8)
                .listBody<UserMessage>()
                .isEqualTo(
                    listOf(
                        UserMessage(
                            id = msg.id,
                            text = "Hello",
                            createdAt = msg.createdAt,
                            fromUserNickName = "Friend",
                            toUserNickName = "Maha"
                        )
                    )
                )
        }
    }

    @Test
    fun `should view sent messages to particular user`() {

        val user = insertUser("Maha")
        val friend = insertUser("Friend")
        val friend2 = insertUser("Friend2")

        val msg = insertMessage("Hello", user.id, friend.id)
        insertMessage("Hello", user.id, friend2.id)

        handle(
            uri = "/api/message/sent/to-user",
            method = HttpMethod.Get,
            headers = mapOf(
                "user" to user.id,
                "to-user" to friend.id
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.OK)
                .contentType(JsonUtf8)
                .listBody<UserMessage>()
                .isEqualTo(
                    listOf(
                        UserMessage(
                            id = msg.id,
                            text = "Hello",
                            createdAt = msg.createdAt,
                            fromUserNickName = "Maha",
                            toUserNickName = "Friend"
                        )
                    )
                )
        }
    }

}