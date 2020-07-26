package messaging.message

import java.lang.RuntimeException

class BodyDeserializationException(msg: String?) : RuntimeException(msg)

class UserIdException(msg: String?) : RuntimeException(msg)

class UserNotFoundException(msg: String?) : RuntimeException(msg)

class UserInputForMessageSendingException(msg: String?) : RuntimeException(msg)
