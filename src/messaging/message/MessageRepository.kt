package messaging.message

import messaging.user.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.koin.core.KoinComponent
import java.time.Instant
import java.util.*

class MessageRepository : KoinComponent {

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
                        fromUserNickName = Users.select { Users.id eq user!! }.map { it[Users.nickname] }.first(),
                        toUserNickName = Users.selectAll().andWhere { Users.id eq to!! }.map { it[Users.nickname] }.first()
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
                        fromUserNickName = Users.select { Users.id eq user!! }.map { it[Users.nickname] }.first(),
                        toUserNickName = Users.selectAll().andWhere { Users.id eq it[Messages.toUser] }.map { it[Users.nickname] }.first()
                    )
                }
        }
    }

    fun getAllReceivedMessages(user: String): List<UserMessage> {
        return transaction {
            Messages
                .selectAll()
                .andWhere { Messages.toUser eq user!! }
                .orderBy(Messages.createdAt, SortOrder.DESC)
                .map {
                    UserMessage(
                        id = it[Messages.id],
                        createdAt = it[Messages.createdAt].let { Instant.ofEpochMilli(it.millis) },
                        text = it[Messages.text],
                        fromUserNickName = Users.selectAll().andWhere { Users.id eq it[Messages.fromUser] }.map { it[Users.nickname] }.first(),
                        toUserNickName = Users.select { Users.id eq user!! }.map { it[Users.nickname] }.first()
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
                .andWhere { Messages.fromUser eq from!! }
                .andWhere { Messages.toUser eq user!! }
                .orderBy(Messages.createdAt, SortOrder.DESC)
                .map {
                    UserMessage(
                        id = it[Messages.id],
                        createdAt = it[Messages.createdAt].let { Instant.ofEpochMilli(it.millis) },
                        text = it[Messages.text],
                        fromUserNickName = Users.selectAll().andWhere { Users.id eq from!! }.map { it[Users.nickname] }.first(),
                        toUserNickName = Users.select { Users.id eq user!! }.map { it[Users.nickname] }.first()
                    )
                }
        }
    }
}