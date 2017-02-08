package com.bob.bkprocess.webapi.service

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by bob on 16/3/22.
 */
class PeopleService(implicit val executionContext: ExecutionContext) {

  var peoples = List(People("1", "1abc", 11, "1abc"), People("2", "2abc", 21, "2abc"))

  def createPeople(people: People): Future[Option[String]] = Future {
    peoples.find(_.id == people.id) match {
      case Some(q) => None
      case None => {
        peoples = peoples :+ people
        Some(people.id)
      }
    }
  }

  def updatePeople(id: String, update: PeopleUpdate): Future[Option[People]] = {

    def updateEntity(people: People): People = {
      val title = update.title
      People(id, people.name, people.age, title)
    }

    findPeople(id).flatMap { maybePeople =>
      maybePeople match {
        case None => Future {
          None // No People found, nothing to update
        }
        case Some(people) =>
          val updatedPeople = updateEntity(people)
          deletePeople(id).flatMap { _ =>
            createPeople(updatedPeople).map(_ => Some(updatedPeople))
          }
      }
    }
  }

  def findPeople(id: String): Future[Option[People]] = Future {
    peoples.find(_.id == id)
  }

  def findPeoples: Future[Option[List[People]]] = Future {
    Some(peoples)
  }

  def deletePeople(id: String): Future[Unit] = Future {
    peoples = peoples.filterNot(_.id == id)
  }
}

case class People(id: String, name: String, age: Int, title: String)

case class PeopleUpdate(id: String, title: String)