package messaging.message

import abstracts.AbstractDBTest
import abstracts.insertMessage
import abstracts.insertUser
import config.lazyInject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import org.koin.core.KoinComponent
import java.time.Instant

class MessageServiceTest : AbstractDBTest(), KoinComponent {
    private val service by lazyInject<MessageService>()

    @Test
    fun `should send message successfully`() {
        val sender = insertUser("Maha")
        val receiver = insertUser("Friend")

        val result = service.send(
            message = ValidatedNewMessage(text = "Hello Friend"),
            from = sender.id,
            to = receiver.id
        )

        assertThat(result).isEqualToIgnoringGivenFields(
            Message(
                id = "",
                text = "Hello Friend",
                createdAt = Instant.now(),
                fromUser = sender.id,
                toUser = receiver.id
            ),
            Message::id.name,
            Message::createdAt.name
        )
    }

    @Test
    fun `should not send message if sender is not null`() {
        val receiver = insertUser("Friend")

        assertThrows<UserIdException>("From user id can't be null or blank") {
            service.send(
                message = ValidatedNewMessage(text = "Hello Friend"),
                from = null,
                to = receiver.id
            )
        }
    }

    @Test
    fun `should not send message to yourself`() {
        val sender = insertUser("Maha")

        assertThrows<UserInputForMessageSendingException>("Message can't be sent to the same sender") {
            service.send(
                message = ValidatedNewMessage(text = "Hello Friend"),
                from = sender.id,
                to = sender.id
            )
        }
    }

    @Test
    fun `should not send message if sender is not blank`() {
        val receiver = insertUser("Friend")

        assertThrows<UserIdException>("From user id can't be null or blank") {
            service.send(
                message = ValidatedNewMessage(text = "Hello Friend"),
                from = "",
                to = receiver.id
            )
        }
    }

    @Test
    fun `should not send message if receiver is not null`() {
        val sender = insertUser("Maha")

        assertThrows<UserIdException>("From user id can't be null or blank") {
            service.send(
                message = ValidatedNewMessage(text = "Hello Friend"),
                from = sender.id,
                to = null
            )
        }
    }

    @Test
    fun `should not send message if receiver is not blank`() {
        val sender = insertUser("Maha")

        assertThrows<UserIdException>("From user id can't be null or blank") {
            service.send(
                message = ValidatedNewMessage(text = "Hello Friend"),
                from = sender.id,
                to = ""
            )
        }
    }

    @Test
    fun `should not send message if sender doesnt exist`() {
        val receiver = insertUser("Friend")

        assertThrows<UserNotFoundException>("From User not found") {
            service.send(
                message = ValidatedNewMessage(text = "Hello Friend"),
                from = "anyid",
                to = receiver.id
            )
        }
    }

    @Test
    fun `should not send message if receiver doesnt exist`() {
        val sender = insertUser("Maha")

        assertThrows<UserNotFoundException>("To User not found") {
            service.send(
                message = ValidatedNewMessage(text = "Hello Friend"),
                from = sender.id,
                to = "anyid"
            )
        }
    }

    @Test
    fun `should return all sent messages by the user sorted by date Desc`() {
        val user1 = insertUser("Maha")
        val user2 = insertUser("Friend1")
        val user3 = insertUser("Friend2")

        val msg1 = insertMessage(message = "Hello Friend1", from = user1.id, to = user2.id)
        val msg2 = insertMessage(message = "Hello Friend2", from = user1.id, to = user3.id)
        insertMessage(message = "Hello Maha", from = user2.id, to = user1.id)

        val result = service.viewAllSentMessages(user1.id)
        assertThat(result).isEqualTo(
            listOf(
                UserMessage(
                    id = msg2.id,
                    createdAt = msg2.createdAt,
                    fromUserNickName = "Maha",
                    toUserNickName = "Friend2",
                    text = "Hello Friend2"
                ),
                UserMessage(
                    id = msg1.id,
                    createdAt = msg1.createdAt,
                    fromUserNickName = "Maha",
                    toUserNickName = "Friend1",
                    text = "Hello Friend1"
                )
            )
        )

    }

    @Test
    fun `should fail retrieving sent message if userid not identified`() {
        assertThrows<UserIdException>("SENDER id can't be null or blank") { service.viewAllSentMessages("") }
    }

    @Test
    fun `should fail retrieving sent message if userid not found`() {
        assertThrows<UserNotFoundException>("SENDER User not found") { service.viewAllSentMessages("userid") }
    }

    @Test
    fun `should return sent messages to particular user sorted by date Desc`() {
        val user1 = insertUser("Maha")
        val user2 = insertUser("Friend1")
        val user3 = insertUser("Friend2")

        insertMessage(message = "Hello Friend1", from = user1.id, to = user2.id)
        val msg2 = insertMessage(message = "Hello Friend2", from = user1.id, to = user3.id)
        insertMessage(message = "Hello Maha", from = user2.id, to = user1.id)

        val result = service.viewSentMessagesToParticularUser(
            user = user1.id,
            to = user3.id
        )
        assertThat(result).isEqualTo(
            listOf(
                UserMessage(
                    id = msg2.id,
                    createdAt = msg2.createdAt,
                    fromUserNickName = "Maha",
                    toUserNickName = "Friend2",
                    text = "Hello Friend2"
                )
            )
        )
    }

    @Test
    fun `should not return sent messages to particular user if sender doesn't exists`() {
        val receiver = insertUser("Friend2")

        assertThrows<UserNotFoundException>("SENDER User not found") {
            service.viewSentMessagesToParticularUser(
                user = "anyid",
                to = receiver.id
            )
        }
    }

    @Test
    fun `should not return sent messages to particular user if sender is blank`() {
        val receiver = insertUser("Friend2")

        assertThrows<UserIdException>("SENDER id can't be null or blank") {
            service.viewSentMessagesToParticularUser(
                user = "",
                to = receiver.id
            )
        }
    }

    @Test
    fun `should not return sent messages to particular user if receiver doesn't exists`() {
        val sender = insertUser("Maha")

        assertThrows<UserNotFoundException>("RECEIVER User not found") {
            service.viewSentMessagesToParticularUser(
                user = sender.id,
                to = "anyid"
            )
        }
    }

    @Test
    fun `should not return sent messages to particular user if receiver is blank`() {
        val sender = insertUser("Maha")

        assertThrows<UserIdException>("RECEIVER id can't be null or blank") {
            service.viewSentMessagesToParticularUser(
                user = sender.id,
                to = ""
            )
        }
    }

    @Test
    fun `should return received messages sorted by date Desc`() {
        val user1 = insertUser("Maha")
        val user2 = insertUser("Friend1")
        val user3 = insertUser("Friend2")

        insertMessage(message = "Hello Friend1", from = user1.id, to = user2.id)
        val msg2 = insertMessage(message = "Hello Maha again", from = user3.id, to = user1.id)
        val msg3 = insertMessage(message = "Hello Maha", from = user2.id, to = user1.id)

        val result = service.viewAllReceivedMessages(
            user = user1.id
        )
        assertThat(result).isEqualTo(
            listOf(
                UserMessage(
                    id = msg3.id,
                    createdAt = msg3.createdAt,
                    fromUserNickName = "Friend1",
                    toUserNickName = "Maha",
                    text = "Hello Maha"
                ),
                UserMessage(
                    id = msg2.id,
                    createdAt = msg2.createdAt,
                    fromUserNickName = "Friend2",
                    toUserNickName = "Maha",
                    text = "Hello Maha again"
                )
            )
        )
    }

    @Test
    fun `should not return received messages  if user doesn't exists`() {
        assertThrows<UserNotFoundException>("RECEIVER User not found") {
            service.viewAllReceivedMessages(
                user = "anyid"
            )
        }
    }

    @Test
    fun `should not return received messages  if user is blank`() {
        assertThrows<UserIdException>("RECEIVER id can't be null or blank") {
            service.viewAllReceivedMessages(
                user = ""
            )
        }
    }

    @Test
    fun `should return received messages from particular user sorted by date Desc`() {
        val user1 = insertUser("Maha")
        val user2 = insertUser("Friend1")
        val user3 = insertUser("Friend2")

        val msg1 = insertMessage(message = "Hello Maha from Friend1", from = user2.id, to = user1.id)
        insertMessage(message = "Hello Maha again", from = user3.id, to = user1.id)
        val msg3 = insertMessage(message = "Hello Maha", from = user2.id, to = user1.id)

        val result = service.viewReceivedMessagesFromUser(
            user = user1.id,
            from = user2.id
        )
        assertThat(result).isEqualTo(
            listOf(
                UserMessage(
                    id = msg3.id,
                    createdAt = msg3.createdAt,
                    fromUserNickName = "Friend1",
                    toUserNickName = "Maha",
                    text = "Hello Maha"
                ),
                UserMessage(
                    id = msg1.id,
                    createdAt = msg1.createdAt,
                    fromUserNickName = "Friend1",
                    toUserNickName = "Maha",
                    text = "Hello Maha from Friend1"
                )
            )
        )
    }

    @Test
    fun `should not return received messages from particular user  if user doesn't exist`() {
        val user1 = insertUser("Maha")

        assertThrows<UserNotFoundException>("RECEIVER User not found") {
            service.viewReceivedMessagesFromUser(
                user = "anyid",
                from = user1.id
            )
        }
    }

    @Test
    fun `should not return received messages from particular user  if sender doesn't exist`() {
        val user1 = insertUser("Maha")

        assertThrows<UserNotFoundException>("SENDER User not found") {
            service.viewReceivedMessagesFromUser(
                user = user1.id,
                from = "any"
            )
        }
    }

}