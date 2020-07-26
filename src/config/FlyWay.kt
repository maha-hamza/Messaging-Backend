package config

import com.uchuhimo.konf.Config
import org.flywaydb.core.Flyway

fun runMigrations(config: Config) {
    Flyway
        .configure()
        .dataSource(
            config[postgres.connectionString],
            config[postgres.user],
            config[postgres.password]
        )
        .load()
        .migrate()
}