package messaging.user

import java.lang.RuntimeException

class UserAlreadyExistedException(msg: String?) : RuntimeException(msg)
