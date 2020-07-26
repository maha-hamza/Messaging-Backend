create TABLE IF NOT EXISTS messages
(
   id            VARCHAR(50)    PRIMARY KEY      NOT NULL,
   text          TEXT                            NOT NULL,
   created_at    TIMESTAMP                       NOT NULL,
   from_user     VARCHAR(50)                     NOT NULL,
   to_user       VARCHAR(50)                     NOT NULL,
   CONSTRAINT fk_messages_from FOREIGN KEY (from_user) REFERENCES users(id),
   CONSTRAINT fk_messages_to   FOREIGN KEY (to_user)   REFERENCES users(id)
);