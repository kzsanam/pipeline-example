package configs

final case class Config(
    port: Int,
    dbConfig: DbConfig
)

final case class DbConfig(
    endpoint: String,
    key: String,
    secret: String,
    table: String
)
