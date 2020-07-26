create TABLE IF NOT EXISTS users
(
   id            VARCHAR(50)    PRIMARY KEY      NOT NULL,
   nickname      VARCHAR(50)                     NOT NULL,
   created_at    TIMESTAMP                       NOT NULL,
   CONSTRAINT unq_packages_name UNIQUE (nickname)
);