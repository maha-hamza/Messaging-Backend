package messaging.message

import messaging.user.Users
import messaging.utils.notBlank
import messaging.utils.validate
import messaging.utils.validateAll
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.time.Instant

object Messages : Table() {
    val id = varchar(name = "id", length = 50).primaryKey()
    val text = text(name = "text")
    val createdAt = datetime("created_at")
    val fromUser = varchar("from_user", 50).references(Users.id)
    val toUser = varchar("to_user", 50).references(Users.id)
}

data class Message(
    val id: String,
    val text: String,
    val createdAt: Instant,
    val fromUser: String,
    val toUser: String
)

data class UserMessage(
    val id: String,
    val text: String,
    val createdAt: Instant,
    val fromUserNickName: String? = null,
    val toUserNickName: String? = null
)

data class NewMessage(val text: String) {

    fun validate(): ValidatedNewMessage {
        return validateAll(
            ::text.validate().notBlank()
        ) { text ->
            ValidatedNewMessage(
                text = text
            )
        }
    }
}

data class ValidatedNewMessage(val text: String)

fun toMessage(resultRow: ResultRow): Message =
    Message(
        id = resultRow[Messages.id],
        text = resultRow[Messages.text],
        fromUser = resultRow[Messages.fromUser],
        toUser = resultRow[Messages.toUser],
        createdAt = resultRow[Messages.createdAt].let { Instant.ofEpochMilli(it.millis) }
    )