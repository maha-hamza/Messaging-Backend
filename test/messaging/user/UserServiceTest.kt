package messaging.user

import abstracts.AbstractDBTest
import abstracts.insertUser
import config.lazyInject
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.koin.core.KoinComponent
import java.time.Instant

class UserServiceTest : AbstractDBTest(), KoinComponent {

    private val service by lazyInject<UserService>()

    @Test
    fun `should insert user successfully if no nickname conflict happens`() {

        val result = service.createUser(
            ValidatedNewUser("Maha")
        )

        assertThat(result.nickname).isEqualTo("Maha")
        assertThat(result.createdAt).isBefore(Instant.now())
    }

    @Test
    fun `should not insert user if nickname conflict happens`() {

        insertUser(nickname = "Maha")
        assertThrows<UserAlreadyExistedException> {
            service.createUser(
                ValidatedNewUser("Maha")
            )
        }
    }
}