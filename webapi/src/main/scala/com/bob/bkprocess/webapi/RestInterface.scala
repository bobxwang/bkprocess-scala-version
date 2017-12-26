package com.bob.bkprocess.webapi

import com.bob.bkprocess.webapi.resource.{BankResource, PeopleResource, Resources}
import com.bob.bkprocess.webapi.service.PeopleService
import com.gettyimages.spray.swagger.SwaggerHttpService
import com.wordnik.swagger.model.ApiInfo
import spray.routing.AuthenticationFailedRejection.{CredentialsRejected, CredentialsMissing}
import spray.routing._
import spray.routing.authentication._

import scala.concurrent.{Future, ExecutionContext}

case class Credentials(id: String, secret: String)

/**
 * 当前登陆用户
 * @param id
 * @param name
 */
case class UserWhoLogin(id: String, name: String)

/**
 * Created by bob on 16/3/23.
 */
class RestInterface(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  override val peopleService: PeopleService = new PeopleService

  val swaggerService = new SwaggerHttpService {

    import scala.reflect.runtime.universe._

    override def apiTypes = Seq(typeOf[PeopleResource], typeOf[BankResource])

    override def apiVersion = "2.0"

    override def baseUrl = "/"

    // let swagger-ui determine the host and port
    override def docsPath = "api-docs"

    override def actorRefFactory = context

    override def apiInfo = Some(new ApiInfo("CreditCard Process Query", "A Service to Query CreditCard Process", "", "sevenz_da_best@hotmail.com", "", ""))

    //authorizations, not used
  }

  private def extractCredentials(ctx: RequestContext) = {
    val queryParams = ctx.request.uri.query.toMap
    for {
      id <- queryParams.get("client_id")
      secret <- queryParams.get("client_secret")
    } yield Credentials(id, secret)
  }

  /**
   * 按照约定，left代表错误，right代表成功
   */
  val authenticator: ContextAuthenticator[UserWhoLogin] = { ctx => Future {
    val maybeCredentials = extractCredentials(ctx)
    maybeCredentials.fold[authentication.Authentication[UserWhoLogin]](
      Left(AuthenticationFailedRejection(CredentialsMissing, List()))
    )(credentials =>
      credentials match {
        case Credentials(clientId, clientToken) => {
          val u = UserWhoLogin(clientId, clientToken)
          Right(u)
        }
        case _ => Left(AuthenticationFailedRejection(CredentialsRejected, List()))
      }
      )
  }
  }

  val needtokenroute = authenticate(authenticator) { user =>
    peopleRoutes
  }

  val notokenroute = bankRoutes

  val swaggerroute = get {
    pathPrefix("") {
      pathEndOrSingleSlash {
        getFromResource("swagger-ui/index.html")
      }
    } ~
      getFromResourceDirectory("swagger-ui")
  } ~ swaggerService.routes

  override def receive: Receive = runRoute(needtokenroute ~ notokenroute ~ swaggerroute)
}