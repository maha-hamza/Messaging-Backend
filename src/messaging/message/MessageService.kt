package messaging.message

import com.fasterxml.jackson.databind.ObjectMapper
import config.MESSAGE_TOPIC
import config.createProducer
import messaging.user.UserRepository
import org.apache.kafka.clients.producer.ProducerRecord
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Remember to add Concurrency Handling in case  update operation is implemented in sometime ,
 * if it's just critical code , rely on DB locking , otherwise use different locks to mark only parts u need to lock
 */
class MessageService : KoinComponent {

    private val repository by inject<MessageRepository>()
    private val userRepository by inject<UserRepository>()
    private val producer = createProducer()

    fun send(
        message: ValidatedNewMessage,
        from: String?,
        to: String?
    ): Message {
        validateUserIds(from, to)
        val msg = repository.insertMessage(
            message = message.text,
            from = from!!,
            to = to!!
        )
        producer.send(ProducerRecord(MESSAGE_TOPIC, ObjectMapper().writeValueAsString(msg))).get()
        return msg
    }

    fun viewSentMessagesToParticularUser(user: String?, to: String?): List<UserMessage> {
        validateUserIds(user, UserType.SENDER)
        validateUserIds(to, UserType.RECEIVER)

        return repository.getSentMessagesToUser(
            user = user!!,
            to = to!!
        )
    }

    fun viewAllSentMessages(user: String?): List<UserMessage> {
        validateUserIds(user, UserType.SENDER)
        return repository.getAllSentMessages(user = user!!)
    }

    fun viewAllReceivedMessages(user: String?): List<UserMessage> {
        validateUserIds(user, UserType.SENDER)

        return repository.getAllReceivedMessages(user = user!!)
    }

    fun viewReceivedMessagesFromUser(user: String?, from: String?): List<UserMessage> {
        validateUserIds(user, UserType.RECEIVER)
        validateUserIds(from, UserType.SENDER)

        return repository.getReceivedMessagesFromUser(
            user = user!!,
            from = from!!
        )
    }

    enum class UserType {
        SENDER, RECEIVER
    }

    private fun validateUserIds(from: String?, to: String?) {
        when {
            from.isNullOrBlank() -> throw UserIdException("From user id can't be null or blank")
            to.isNullOrBlank() -> throw UserIdException("to user id can't be null or blank")
            !userRepository.isUserExists(from) -> throw UserNotFoundException("From User not found")
            !userRepository.isUserExists(to) -> throw UserNotFoundException("To User not found")
            from == to -> throw UserInputForMessageSendingException("Message can't be sent to the same sender")
        }
    }

    private fun validateUserIds(user: String?, type: UserType) {
        when {
            user.isNullOrBlank() -> throw UserIdException("$type id can't be null or blank")
            !userRepository.isUserExists(user) -> throw UserNotFoundException("$type User not found")
        }
    }
}







