package config

import kafkaConfig
import loadKafkaConfig
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.StringSerializer
import java.lang.Exception
import java.util.*


private val config = loadKafkaConfig()
val MESSAGE_TOPIC = config[kafkaConfig.topic]

fun createProducer(): Producer<String, String>? {

    val props = Properties()
    props["bootstrap.servers"] = config[kafkaConfig.broker]
    props["key.serializer"] = StringSerializer::class.java.canonicalName
    props["value.serializer"] = StringSerializer::class.java.canonicalName
    props["request.timeout.ms"] = 1000
    // val result = createTopicIfNotExists(config[kafkaConfig.broker], config[kafkaConfig.topic])
    //return if (result) KafkaProducer<String, String>(props) else null
    return KafkaProducer<String, String>(props)
}

private fun createTopicIfNotExists(bootstrapServers: String, topic: String): Boolean {
    val properties = Properties()
    properties["bootstrap.servers"] = bootstrapServers
    properties["connections.max.idle.ms"] = 1000
    properties["request.timeout.ms"] = 1000
    AdminClient.create(properties).use { client ->
        try {
            if (!client.listTopics().names().get().contains(topic)) {
                client.createTopics(
                    listOf(
                        NewTopic(topic, 1, 1.toShort())
                    )
                ).all()
            }
        } catch (e: Exception) {
            return false
        }
    }
    return true
}