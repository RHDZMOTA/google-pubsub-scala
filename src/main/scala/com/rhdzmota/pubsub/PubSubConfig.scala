package com.rhdzmota.pubsub

import java.security.{KeyFactory, PrivateKey}
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

case class PubSubConfig(privateKeyString: String, projectId: String, apiKey: String, clientEmail: String) {
  val privateKey: PrivateKey = KeyFactory.getInstance("RSA").generatePrivate(
    new PKCS8EncodedKeySpec(Base64.getDecoder.decode(privateKeyString)))
}

object PubSubConfig{
  def fromEnv(privateKeyStringEnv: String, projectIdEnv: String, apiKeyEnv: String, clientEmailEnv: String): Option[PubSubConfig] = for {
    privateKeyString  <- sys.env.get(privateKeyStringEnv)
    projectId         <- sys.env.get(projectIdEnv)
    apiKey            <- sys.env.get(apiKeyEnv)
    clientEmail       <- sys.env.get(clientEmailEnv)
  } yield PubSubConfig(privateKeyString, projectId, apiKey, clientEmail)
}