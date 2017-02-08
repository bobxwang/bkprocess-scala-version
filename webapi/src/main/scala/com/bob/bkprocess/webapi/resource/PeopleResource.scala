package com.bob.bkprocess.webapi.resource

import com.bob.bkprocess.webapi.routing.BBHttpService
import com.bob.bkprocess.webapi.service.{People, PeopleService, PeopleUpdate}
import com.wordnik.swagger.annotations._
import spray.routing.Route

@Api(value = "peoples", description = "operation about people")
trait PeopleResource extends BBHttpService {

  val peopleService: PeopleService

  def peopleRoutes: Route = pathPrefix("peoples") {
    postPerson ~ getPerson ~ deletePerson ~ updatePerson ~ getAllPerson
  }

  @ApiOperation(value = "Update a people by ID", notes = "Returns a people based on ID", httpMethod = "PUT", response = classOf[People])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "PeopleUpdate", dataType = "com.bob.bkprocess.webapi.service.PeopleUpdate", required = true, paramType = "body")
    , new ApiImplicitParam(name = "id", value = "ID of people that needs to be fetched", required = true, dataType = "integer", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Pet not found"),
    new ApiResponse(code = 400, message = "Invalid ID supplied")
  ))
  def updatePerson = path(Segment) { id =>
    put {
      entity(as[PeopleUpdate]) { update =>
        if (id != update.id) {
          reject // 如果body体要修改的标识符跟path占位符不一样，则拒绝
        } else {
          complete(peopleService.updatePeople(id, update))
        }
      }
    }
  }

  @ApiOperation(value = "Find a people by ID", notes = "Returns a people based on ID", httpMethod = "GET", response = classOf[People])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "ID of people that needs to be fetched", required = true, dataType = "integer", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Pet not found"),
    new ApiResponse(code = 400, message = "Invalid ID supplied")
  ))
  def getPerson = get {
    path(Segment) { id =>
      complete(peopleService.findPeople(id))
    }
  }

  @ApiOperation(value = "Get All people", notes = "Returns All People", httpMethod = "GET", response = classOf[List[People]])
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Pet not found"),
    new ApiResponse(code = 400, message = "Invalid ID supplied")
  ))
  def getAllPerson = get {
    complete(peopleService.findPeoples)
  }

  @ApiOperation(value = "Delete a people by ID", notes = "Delete a people based on ID", httpMethod = "DELETE", response = classOf[Unit])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "id", value = "ID of people that needs to be delete", required = true, dataType = "integer", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Pet not found"),
    new ApiResponse(code = 400, message = "Invalid ID supplied")
  ))
  def deletePerson = delete {
    path(Segment) { id =>
      complete(204, peopleService.deletePeople(id))
    }
  }

  @ApiOperation(value = "Post a People", notes = "", nickname = "postPeople", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "People Who to Add", dataType = "com.bob.bkprocess.webapi.service.People", required = true, paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "People got created"),
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def postPerson = post {
    entity(as[People]) { x =>
      completeWithLocationHeader(
        resourceId = peopleService.createPeople(x),
        ifDefinedStatus = 201, ifEmptyStatus = 409)
    }
  }
}