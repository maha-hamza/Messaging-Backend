package config

import com.uchuhimo.konf.Config

import messaging.message.MessageController
import messaging.message.MessageRepository
import messaging.message.MessageService
import messaging.user.UserController
import messaging.user.UserRepository
import messaging.user.UserService
import org.jetbrains.exposed.sql.Database
import org.koin.Logger.slf4jLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import javax.sql.DataSource

fun initKoin(config: Config) {
    startKoin {
        modules(modules(config))
        slf4jLogger()
    }
}

fun modules(config: Config, overrides: List<Module> = emptyList()): List<Module> {
    return listOf(
        module {
            single { config }

            single { connectionPool(config) }
            single { Database.connect(get<DataSource>()) }

            single { MessageController() }
            single { MessageService() }
            single { MessageRepository() }

            single { UserController() }
            single { UserService() }
            single { UserRepository() }
        }
    ) + overrides
}

inline fun <reified T : Any> inject(): T {
    return GlobalContext.get().koin.get()
}

inline fun <reified T : Any> lazyInject(): Lazy<T> {
    return lazy { inject<T>() }
}