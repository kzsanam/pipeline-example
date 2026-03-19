package configs

final case class Config(
    aws: Aws
)

final case class Aws(
    endpoint: String,
    accessKey: String,
    secretKey: String
)
