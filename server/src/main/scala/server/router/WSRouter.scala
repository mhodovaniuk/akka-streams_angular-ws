package server.router

import akka.NotUsed
import akka.actor.{ActorSystem, PoisonPill}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path, _}
import akka.http.scaladsl.server.Route
import akka.stream._
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.google.inject.{Inject, Injector}
import com.typesafe.scalalogging.LazyLogging
import server.actor.UserActor
import server.actor.UserActor._
import server.model.Item
import server.module.akka.GuiceAkkaExtension
import spray.json.{DefaultJsonProtocol, _}

class WSRouter @Inject()(injector: Injector, actorSystem: ActorSystem, implicit val actorMaterializer: ActorMaterializer) extends DefaultJsonProtocol with LazyLogging {
  implicit val itemFormat = new RootJsonFormat[Item] {
    override def write(obj: Item): JsValue = {
      JsObject(
        obj.id.map(id => Map[String, JsValue]("id" -> JsNumber(id))).getOrElse(Map())
          ++
          Map(
            "name" -> JsString(obj.name))
      )
    }

    override def read(json: JsValue): Item = {
      val fields = json.asJsObject.fields
      val id = fields.get("id").flatMap({
        case JsNumber(idValue) => Some(idValue.longValue())
        case _ => None
      })
      val name = fields.get("name").map({ case JsString(nameValue) => nameValue }).get
      Item(id, name)
    }
  }

  implicit val pingRequestFormat = jsonFormat2(PingRequest)
  implicit val pingResponseFormat = jsonFormat2(PingResponse)

  implicit val authRequestFormat = jsonFormat3(AuthRequest)
  implicit val authSuccessResponseFormat = jsonFormat2(AuthSuccessResponse)
  implicit val authFailureResponseFormat = jsonFormat1(AuthFailureResponse)

  implicit val subscribeRequestFormat = jsonFormat1(SubscribeRequest)
  implicit val subscribeResponseFormat = jsonFormat2(SubscribeResponse)
  implicit val unsubscribeResponseFormat = jsonFormat1(UnsubscribeRequest)

  implicit val addItemRequestFormat = jsonFormat3(AddUpdateItemRequest)
  implicit val removeItemRequestFormat = jsonFormat2(RemoveItemRequest)
  implicit val itemAddedResponseFormat = jsonFormat3(ItemAddedUpdatedResponse)
  implicit val itemRemovedResponseFormat = jsonFormat2(ItemRemovedResponse)

  implicit val notEnoughPrivilegesResponse = jsonFormat1(NotEnoughPrivilegesResponse)

  implicit val wsResponseFormat = new RootJsonFormat[WSResponse] {
    override def write(obj: WSResponse): JsValue = {
      obj match {
        case r: PingResponse => r.toJson.asJsObject

        case r: AuthSuccessResponse => r.toJson.asJsObject
        case r: AuthFailureResponse => r.toJson.asJsObject

        case r: SubscribeResponse => r.toJson.asJsObject

        case r: ItemAddedUpdatedResponse => r.toJson.asJsObject
        case r: ItemRemovedResponse => r.toJson.asJsObject

        case r: NotEnoughPrivilegesResponse => r.toJson.asJsObject
      }
    }

    override def read(json: JsValue): WSResponse = throw new UnsupportedOperationException
  }

  val route: Route =
    path("api" / "ws") {
      handleWebSocketMessages(flow)
    }

  def flow: Flow[Message, Message, _] = {
    val userActor = actorSystem.actorOf(GuiceAkkaExtension(actorSystem).props(UserActor.name))
    logger.debug("User connected.")

    val incomingMessages: Sink[Message, NotUsed] =
      Flow[Message].map {
        case tm: TextMessage.Strict =>
          val in = tm.getStrictText
          logger.trace(s"Request received: '$in'.")
          in
      }
        .map(in => {
          val jsonObject = in.parseJson.asJsObject
          logger.trace("Request JSON was successfully parsed.")
          jsonObject
        })
        .map(jsonObject => {
          val result: (Option[String], JsObject) = jsonObject.fields.get("$type") match {
            case Some(JsString(requestType)) =>
              logger.debug(s"Request type is '$requestType'.")
              (Some(requestType), jsonObject)
            case _ =>
              logger.debug("Unable to recognize request type.")
              (None, jsonObject)
          }
          result
        })
        .filter({
          case (Some(_), _) => true
          case _ => false
        })
        .map({
          case (Some(requestType), jsonObject) => (requestType, jsonObject)
        })
        .map({
          case ("ping", jsonObject) => jsonObject.convertTo[PingRequest]
          case ("login", jsonObject) => jsonObject.convertTo[AuthRequest]
          case ("subscribe_items", jsonObject) => jsonObject.convertTo[SubscribeRequest]
          case ("unsubscribe_items", jsonObject) => jsonObject.convertTo[UnsubscribeRequest]
          case ("add_update_item", jsonObject) => jsonObject.convertTo[AddUpdateItemRequest]
          case ("remove_item", jsonObject) => jsonObject.convertTo[RemoveItemRequest]
          case _ =>
            logger.debug("Request type is unknown.")
            UnknownRequest()
        }).to(Sink.actorRef[WSRequest](userActor, PoisonPill))

    val outgoingMessages: Source[Message, NotUsed] =
      Source.actorRef[WSResponse](10, OverflowStrategy.fail)
        .mapMaterializedValue { outActor =>
          userActor ! Connected(outActor)
          NotUsed
        }
        .map((outMsg: WSResponse) => {
          val response = outMsg.toJson.compactPrint
          logger.trace(s"Response sent: $response")
          TextMessage(response)
        })

    Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
  }
}
