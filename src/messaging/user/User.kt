package messaging.user

import messaging.utils.notBlank
import messaging.utils.validate
import messaging.utils.validateAll
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import java.time.Instant

object Users : Table() {
    val id = varchar(name = "id", length = 50).primaryKey()
    val nickname = varchar("nickname", 50)
    val createdAt = datetime("created_at")
}

data class User(val id: String, val nickname: String, val createdAt: Instant)

data class NewUser(val nickname: String) {
    fun validate(): ValidatedNewUser {
        return validateAll(
            ::nickname.validate().notBlank()
        ) { nickname ->
            ValidatedNewUser(
                nickname = nickname
            )
        }
    }
}

data class ValidatedNewUser(val nickname: String)

fun toUser(resultRow: ResultRow): User =
    User(
        id = resultRow[Users.id],
        nickname = resultRow[Users.nickname],
        createdAt = resultRow[Users.createdAt].let { Instant.ofEpochMilli(it.millis) }
    )