# PUBSUB Scala

Utility code for using Google PubSub with Scala (Akka Streams).

## Usage

**Note**: You need to create a google service account for pubsub to have the access keys for the project.

### Configuration variables

You can put the relevant configuration variables in the ENV and create a pubsub config object:
```scala
val config: Option[PubSubConfig] = PubSubConfig.fromEnv(
  "PUBSUB_PRIVATE_KEY", "GOOGLE_PROJECT_ID",
  "PUBSUB_API_ID", "GOOGLE_SERVICE_ACCOUNT_EMAIL")
```

Or pass them directly (not recommended):
```scala
val config: PubSubConfig = PubSubConfig(
  "my-private-key", "my-google-ptoject-id",
  "my-pubsub-api-id", "my-google-service-account-email")
```

Or use a configuration file with the credentials and initialize the object later (e.g. `application.conf`).

## Producer

Example of a producer/publisher:

```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.rhdzmota.pubsub.{PubSubConfig, PubSubProducer}

import scala.concurrent.Future

object Example {

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem()
    implicit val actorMaterializer = ActorMaterializer()
    
    val pubSubConfig = PubSubConfig.fromEnv(
      "PUBSUB_PRIVATE_KEY", "GOOGLE_PROJECT_ID",
      "PUBSUB_API_ID", "GOOGLE_SERVICE_ACCOUNT_EMAIL")
      
    val exampleTopic = "my-pubsub-topic"
    val exampleData = "Data payload for pubsub"
    val exampleMessageId = "message-id-example"
    val exampleAttributes = Some(Map("key" -> "value"))
    
    val result: Option[Future[Seq[Seq[String]]]] = pubSubConfig.map(config => {
      val pubSubProducer= PubSubProducer(config)
      pubSubProducer.publish(exampleTopic, exampleData,
        exampleMessageId, exampleAttributes)
    })
    
    result match {
      case Some(future) => future.onComplete(println(_))
      case None => println("Missing env variables")
    }
  }
}

```

## Consumer

Example of a consumer/subscriber:
```scala
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.googlecloud.pubsub.ReceivedMessage
import com.rhdzmota.pubsub.{PubSubConfig, PubSubConsumer}

object Example {

  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem()
    implicit val actorMaterializer = ActorMaterializer()
    
    val pubSubConfig = PubSubConfig.fromEnv(
      "PUBSUB_PRIVATE_KEY", "GOOGLE_PROJECT_ID",
      "PUBSUB_API_ID", "GOOGLE_SERVICE_ACCOUNT_EMAIL")
      
    val exampleSubscription = "my-subscription"
    val printMessageFunction: ReceivedMessage => Unit = 
      (receivedMessage: ReceivedMessage) => println(receivedMessage.message,toString)
      
    pubSubConfig 
    
  }
}

```

