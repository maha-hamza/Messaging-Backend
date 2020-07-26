package messaging.user

import abstracts.*
import org.junit.jupiter.api.Test
import abstracts.ProjectionAssertions.Companion.assertThat
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import java.time.Instant

class UserControllerTest : AbstractDBTest() {

    @Test
    fun `should insert user successfully if no duplication conflicts happen`() {

        handle(
            uri = "/api/user",
            method = HttpMethod.Post,
            body = NewUser(
                nickname = "Maha"
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.Created)
                .contentType(JsonUtf8)
                .body<User>()
                .isEqualToIgnoringGivenFields(
                    User(
                        id = "",
                        nickname = "Maha",
                        createdAt = Instant.now()
                    ),
                    User::createdAt.name,
                    User::id.name
                )
        }
    }

    @Test
    fun `should not insert User if duplication conflicts happen`() {

        insertUser(nickname = "Maha")
        handle(
            uri = "/api/user",
            method = HttpMethod.Post,
            body = NewUser(
                nickname = "Maha"
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.BadRequest)
                .contentType(PlainUtf8)
                .body<String>()
                .isEqualTo("Nickname Maha existed before")
        }
    }

    @Test
    fun `should not insert User if nickname provided is blank`() {

        handle(
            uri = "/api/user",
            method = HttpMethod.Post,
            body = NewUser(
                nickname = ""
            )
        ) {
            assertThat(response)
                .status(HttpStatusCode.BadRequest)
                .contentType(JsonUtf8)
                .body<String>()
                .isEqualTo(
                    """
                    [ {
                      "field" : "nickname",
                      "type" : "NotBlank"
                    } ]
                """.trimIndent()
                )
        }
    }
}