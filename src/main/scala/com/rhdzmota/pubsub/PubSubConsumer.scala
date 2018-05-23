package com.rhdzmota.pubsub

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.{Done, NotUsed}
import akka.stream.alpakka.googlecloud.pubsub.{AcknowledgeRequest, ReceivedMessage}
import akka.stream.alpakka.googlecloud.pubsub.scaladsl.GooglePubSub
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration._

case class PubSubConsumer(config: PubSubConfig)(implicit actorSystem: ActorSystem, materializer: Materializer) {

  def source(subscription: String): Source[ReceivedMessage, NotUsed] =
    GooglePubSub.subscribe(config.projectId, config.apiKey, config.clientEmail, config.privateKey, subscription)

  def acknowledgeSink(subscription: String): Sink[AcknowledgeRequest, Future[Done]] =
    GooglePubSub.acknowledge(config.projectId, config.apiKey, config.clientEmail, config.privateKey, subscription)

  def subscribe[A](messageTransform: ReceivedMessage => A)(subscription: String): RunnableGraph[NotUsed] =
    source(subscription).map {message: ReceivedMessage => messageTransform(message); message.ackId}
      .groupedWithin(10, 5.seconds)
      .map(AcknowledgeRequest.apply)
      .to(acknowledgeSink(subscription))

  def identitySubscribe: String => RunnableGraph[NotUsed] = subscribe((message: ReceivedMessage) => message)
  def printlnSubscribe: String => RunnableGraph[NotUsed]  = subscribe((message: ReceivedMessage) =>
    println(message.message.toString))
}
