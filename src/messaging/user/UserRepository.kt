package messaging.user

import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.koin.core.KoinComponent
import java.util.*

class UserRepository : KoinComponent {

    fun insertUser(nickname: String): User {
        return transaction {
            Users.insert {
                it[id] = UUID.randomUUID().toString()
                it[createdAt] = DateTime.now()
                it[Users.nickname] = nickname
            }.resultedValues!!
                .map(::toUser)
                .first()
        }
    }

    fun isNicknameExists(nickname: String): Boolean {
        return transaction {
            Users.select { Users.nickname eq nickname }.firstOrNull() != null
        }
    }

    fun isUserExists(id: String): Boolean {
        return transaction {
            Users.select { Users.id eq id }.firstOrNull() != null
        }
    }
}