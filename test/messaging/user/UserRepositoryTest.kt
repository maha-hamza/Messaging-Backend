package messaging.user

import abstracts.AbstractDBTest
import abstracts.insertUser
import config.lazyInject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.koin.core.KoinComponent

class UserRepositoryTest : AbstractDBTest(), KoinComponent {
    private val repository by lazyInject<UserRepository>()

    @Test
    fun `should check that user exists (return true)`() {
        insertUser(nickname = "Maha")
        val result = repository.isNicknameExists("Maha")
        assertTrue(result)
    }

    @Test
    fun `should check that user not exists (return false)`() {
        val result = repository.isNicknameExists("Maha")
        assertFalse(result)
    }

}