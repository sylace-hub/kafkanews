import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import scala.collection.JavaConverters._

object Main {

  def main(args: Array[String]): Unit = {

    /* Producer */
    val producerProperties = new Properties()

    producerProperties.put("bootstrap.servers", "http://127.0.0.1:9092")
    producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](producerProperties)
    /* */

    /* Consumer */
    val consumerProperties = new Properties()

    consumerProperties.put("bootstrap.servers", "http://127.0.0.1:9092")
    consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.put("group.id", "foobar-group")

    val consumer = new KafkaConsumer[String, String](consumerProperties)

    consumer.subscribe(Collections.singletonList("foobar"))
    /* */

    while (true) {
      /* Writing */
      /*
      val curlRequest = Seq("curl", "https://newsapi.org/v2/everything", "-G",
        "-d", "q=SpaceX",
        "-d", "from=2019-04-16",
        "-d", "sortBy=popularity",
        "-d", "apiKey=4e86ff31690e4f9b9b65c06ae033cc28")
      curlRequest.!*/
      val record = new ProducerRecord[String, String]("foobar", "key", "Hello World !")

      producer.send(record)

      producer.send(record, (m:RecordMetadata, e:Exception) => {
        println()
        println("*** Producer callback ***")
        println("----")
        println("Checksum : " + m.checksum())
        println("Offset : " + m.offset())
        println("Partition : " + m.partition())
        println("Serialized key size : " + m.serializedKeySize())
        println("Serialized value size : " + m.serializedValueSize())
        println("Timestamp : " + m.timestamp())
        println("Topic : " + m.topic())
        println("Exception " + e)
      })
      /* */

      /* Reading */
      val records = consumer.poll(100)

      for (record:ConsumerRecord[String, String] <- records.asScala) {
        println()
        println("*** Consumer reading ***")
        println("----")
        println("Checksum : " + record.checksum())
        println("Offset : " + record.offset())
        println("Partition : " + record.partition())
        println("Serialized key size : " + record.serializedKeySize())
        println("Serialized value size : " + record.serializedValueSize())
        println("Timestamp : " + record.timestamp())
        println("Timestamp type : " + record.timestampType())
        println("----")
        println("Topic : " + record.topic())
        println("Value : " + record.value())
      }
      /* */
    }

    producer.close()
    consumer.close()
  }

}