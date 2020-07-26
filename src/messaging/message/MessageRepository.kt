package messaging.message

import messaging.user.UserRepository
import messaging.user.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.time.Instant
import java.util.*

class MessageRepository : KoinComponent {

    private val userRepository by inject<UserRepository>()

    fun insertMessage(
        message: String,
        from: String,
        to: String
    ): Message {
        return transaction {
            Messages.insert {
                it[id] = UUID.randomUUID().toString()
                it[createdAt] = DateTime.now()
                it[text] = message
                it[fromUser] = from
                it[toUser] = to
            }.resultedValues!!
                .map(::toMessage)
                .first()
        }
    }

    fun getSentMessagesToUser(
        user: String,
        to: String
    ): List<UserMessage> {
        return transaction {
            Messages
                .selectAll()
                .andWhere { Messages.fromUser eq user }
                .andWhere { Messages.toUser eq to }
                .orderBy(Messages.createdAt, SortOrder.DESC)
                .map {
                    UserMessage(
                        id = it[Messages.id],
                        createdAt = it[Messages.createdAt].let { Instant.ofEpochMilli(it.millis) },
                        text = it[Messages.text],
                        fromUserNickName = userRepository.getUserNickname(user),
                        toUserNickName = userRepository.getUserNickname(to)
                    )
                }
        }
    }

    fun getAllSentMessages(user: String): List<UserMessage> {
        return transaction {
            Messages.selectAll()
                .andWhere { Messages.fromUser eq user }
                .orderBy(Messages.createdAt, SortOrder.DESC)
                .map {
                    UserMessage(
                        id = it[Messages.id],
                        createdAt = it[Messages.createdAt].let { Instant.ofEpochMilli(it.millis) },
                        text = it[Messages.text],
                        fromUserNickName = userRepository.getUserNickname(user),
                        toUserNickName = userRepository.getUserNickname(it[Messages.toUser])
                    )
                }
        }
    }

    fun getAllReceivedMessages(user: String): List<UserMessage> {
        return transaction {
            Messages
                .selectAll()
                .andWhere { Messages.toUser eq user }
                .orderBy(Messages.createdAt, SortOrder.DESC)
                .map {
                    UserMessage(
                        id = it[Messages.id],
                        createdAt = it[Messages.createdAt].let { Instant.ofEpochMilli(it.millis) },
                        text = it[Messages.text],
                        fromUserNickName = userRepository.getUserNickname(it[Messages.fromUser]),
                        toUserNickName = userRepository.getUserNickname(user)
                    )
                }
        }
    }

    fun getReceivedMessagesFromUser(
        user: String,
        from: String
    ): List<UserMessage> {
        return transaction {
            Messages
                .selectAll()
                .andWhere { Messages.fromUser eq from }
                .andWhere { Messages.toUser eq user }
                .orderBy(Messages.createdAt, SortOrder.DESC)
                .map {
                    UserMessage(
                        id = it[Messages.id],
                        createdAt = it[Messages.createdAt].let { Instant.ofEpochMilli(it.millis) },
                        text = it[Messages.text],
                        fromUserNickName = userRepository.getUserNickname(from),
                        toUserNickName = userRepository.getUserNickname(user)
                    )
                }
        }
    }
}