package config

import com.uchuhimo.konf.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun connectionPool(cfg: Config): DataSource {
    val config = HikariConfig().apply {
        jdbcUrl = cfg[hdb.connectionString]
        connectionTimeout = 5000
        maximumPoolSize = 5
    }

    return HikariDataSource(config)
}