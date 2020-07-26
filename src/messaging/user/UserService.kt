package messaging.user

import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Remember to add Concurrency Handling in case  update operation is implemented in sometime ,
 * if it's just critical code , rely on DB locking , otherwise use different locks to mark only parts u need to lock
 */
class UserService : KoinComponent {

    private val repository by inject<UserRepository>()

    fun createUser(user: ValidatedNewUser): User {
        return when (repository.isNicknameExists(user.nickname)) {
            true -> throw UserAlreadyExistedException("Nickname ${user.nickname} existed before")
            false -> repository.insertUser(nickname = user.nickname)
        }
    }

    fun getAllUsers(): List<User> {
        return repository.getAllUsers()
    }

}







