package models.services

import play.api.inject.guice.GuiceInjectorBuilder
import play.api.inject.bind
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatest.concurrent._

object ServiceTest extends Tag("org.northrop.leanne.play.mantra.tags.ServiceTest")

abstract class ServiceSpec extends FlatSpec with ScalaFutures with MockFactory {
}