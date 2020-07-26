package abstracts

import messaging.message.Message
import messaging.message.Messages
import messaging.message.toMessage
import messaging.user.User
import messaging.user.Users
import messaging.user.toUser
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.util.*

fun insertUser(nickname: String): User {
    return transaction {
        transaction {
            Users.insert {
                it[id] = UUID.randomUUID().toString()
                it[createdAt] = DateTime.now()
                it[Users.nickname] = nickname
            }.resultedValues!!
                .map(::toUser)
                .first()
        }
    }
}

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
