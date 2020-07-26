package config

import com.uchuhimo.konf.Config
import org.flywaydb.core.Flyway

fun runMigrations(config: Config) {
    Flyway
        .configure()
        .dataSource(
            config[hdb.connectionString],
            config[hdb.user],
            config[hdb.password]
        )
        .load()
        .migrate()
}