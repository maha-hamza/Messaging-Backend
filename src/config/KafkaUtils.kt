package config

import kafkaConfig
import loadKafkaConfig
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*


private val config = loadKafkaConfig()
val MESSAGE_TOPIC = config[kafkaConfig.topic]

fun createProducer(): Producer<String, String> {

    val props = Properties()
    props["bootstrap.servers"] = config[kafkaConfig.broker]
    props["key.serializer"] = StringSerializer::class.java.canonicalName
    props["value.serializer"] = StringSerializer::class.java.canonicalName
    createTopicIfNotExists(config[kafkaConfig.broker], config[kafkaConfig.topic])
    return KafkaProducer<String, String>(props)
}

private fun createTopicIfNotExists(bootstrapServers: String, topic: String) {
    val properties = Properties()
    properties["bootstrap.servers"] = bootstrapServers
    properties["connections.max.idle.ms"] = 10000
    properties["request.timeout.ms"] = 5000
    AdminClient.create(properties).use { client ->
        if (!client.listTopics().names().get().contains(topic)) {
            client.createTopics(
                listOf(
                    NewTopic(topic, 1, 1.toShort())
                )
            ).all().get()
        }
    }
}