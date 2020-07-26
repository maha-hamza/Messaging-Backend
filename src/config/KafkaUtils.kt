package config

import loadKafkaConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import java.util.*

private val config = loadKafkaConfig()
val MESSAGE_TOPIC = config[kafkaConfig.topic]

fun createProducer(): Producer<String, String> {

    val props = Properties()
    props["bootstrap.servers"] = config[kafkaConfig.broker]
    props["key.serializer"] = StringSerializer::class.java.canonicalName
    props["value.serializer"] = StringSerializer::class.java.canonicalName
    return KafkaProducer<String, String>(props)
}