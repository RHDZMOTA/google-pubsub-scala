package com.rhdzmota.pubsub

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.googlecloud.pubsub.{PubSubMessage, PublishRequest}
import akka.stream.alpakka.googlecloud.pubsub.scaladsl.GooglePubSub
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future
import scala.collection.immutable.Seq

case class PubSubProducer(config: PubSubConfig)(implicit actorSystem: ActorSystem, materializer: Materializer) {

  def source(request: PublishRequest): Source[PublishRequest, NotUsed] = Source.single(request)

  def publishFlow(topic: String): Flow[PublishRequest, Seq[String], NotUsed] =
    GooglePubSub.publish(config.projectId, config.apiKey, config.clientEmail, config.privateKey, topic)

  def publish(topic: String, messageId: String, data: String): Future[Seq[Seq[String]]] = {
    val publishMessage = PubSubMessage(messageId, data)
    val publishRequest = PublishRequest(Seq(publishMessage))
    source(publishRequest).via(publishFlow(topic)).runWith(Sink.seq)
  }
}
