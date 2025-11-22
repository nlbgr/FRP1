package frp.assignments

import scala.collection.immutable.HashMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

// comment out to have it in every file so every file works on its own.
// Else this would be defined multiple times
//given ExecutionContext = ExecutionContext.global


object Exc1Monads:
  // #################### 1.1 ####################

  // 1.1a)
  case class User(var id: Int, var name: String, var email: String)

  // 1.1b)
  val database: Map[Int, User] = HashMap[Int, User](
    1 -> User(1, "Test 1", "test1@gmail.com"),
    2 -> User(2, "Test 2", "test2@gmail.com"),
    3 -> User(3, "Test 3", "test3@gmail.com"),
    4 -> User(4, "Test 4", "test4@gmail.com"),
    5 -> User(5, "", "test5@gmail.com"),
    6 -> User(6, "Test 6", "failure.com")
  )

  // 1.1c)
  def fetchUserById(id: Int): Future[User] = {
    database.get(id) match
      case Some(user) => Future.successful(user)
      case _ => Future.failed(IllegalArgumentException(s"User with id $id does not exist"))
  }

  // 1.1d)
  def validateUser(user: User): Try[User] = {
    val emailPattern: Regex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$".r

    if (user.name.isEmpty) {
      Failure(IllegalArgumentException(s"username is empty"))
    } else if (!emailPattern.matches(user.email)) {
      Failure(IllegalArgumentException(s"email does not match is empty"))
    } else {
      Success(user)
    }
  }

  // 1.1e)
  def fetchAndValidateUser(id: Int): Future[User] = {
    val f: Future[User] = fetchUserById(id)

    val validatedUser: Future[User] = f.flatMap(user => {
      validateUser(user) match
        case Success(value) => Future.successful(user)
        case Failure(ex) => Future.failed(ex)
    })

    validatedUser
  }

  // 1.1f)
  def printResult(): Unit = {
    val f1: Future[User] = fetchAndValidateUser(1)
    val f2: Future[User] = fetchAndValidateUser(5)
    val f3: Future[User] = fetchAndValidateUser(6)

    val d1 = f1.andThen {
      case Success(value) => println(s"user1: $value")
      case Failure(ex) => println(s"ex1: $ex")
    }
    val d2 = f2.andThen {
      case Success(value) => println(s"user5: $value")
      case Failure(ex) => println(s"ex5: $ex")
    }
    val d3 = f3.andThen {
      case Success(value) => println(s"user6: $value")
      case Failure(ex) => println(s"ex6: $ex")
    }

    Await.ready(d1, Duration.Inf)
    Await.ready(d2, Duration.Inf)
    Await.ready(d3, Duration.Inf)
  }

  def main(args: Array[String]): Unit = {
    printResult()
  }