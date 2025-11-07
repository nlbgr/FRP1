package stream

import reduce.*
import reduce.Monoid
import reduce.Monoid.intPlusMonoid

sealed trait Stream[+A] {
  val isEmpty: Boolean

  def map[B](mapper: A => B): Stream[B] =
    this match {
      case Empty => Empty
      case Cons(hdFn, tlFn) => Cons(() => mapper(hdFn()), () => tlFn().map(mapper))
    }

  def take(n: Int): Stream[A] = {
    if (n == 0) {
      Empty
    } else {
      this match {
        case Empty => Empty
        case Cons(hdFn, tlFn) => Cons(hdFn, () => tlFn().take(n - 1))
      }
    }
  }

  def filter(pred: A => Boolean): Stream[A] =
    this match{
      case Empty => Empty
      case Cons(hdFn, tlFn) => {
        val hd = hdFn()
        if (pred(hd)) {
          Cons(() => hd, () => tlFn().filter(pred))
        } else {
          tlFn().filter(pred)
        }
      }
    }

  // TODO: Task 9.3: Methods in trait Stream returning results
  def head: A = headOption.get
  def headOption: Option[A] = this match {
    case Empty => None
    case Cons(hdFn, tlFn) => Some(hdFn())
  }

  def tail: Stream[A] = tailOption.get
  def tailOption: Option[Stream[A]] = this match {
    case Empty => None
    case Cons(hdFn, tlFn) => Some(tlFn())
  }

  def forEach(action: A => Unit): Unit = this match {
    case Empty => {}
    case Cons(hdFn, tlFn) => {
      action(hdFn())
      tlFn().forEach(action)
    }
  }
  def toList: List[A] = this match {
    case Empty => List()
    case Cons(hdFn, tlFn) => hdFn()::tlFn().toList
  }

  def reduceMap[R](mapper: A => R) (using monoid: Monoid[R]): R = this match {
    case Empty => monoid.zero
    case Cons(hdFn, tlFn) => monoid.op(mapper(hdFn()), tlFn().reduceMap(mapper))
  }
  def count: Long = reduceMap(x => 1)(using intPlusMonoid)
}

case object Empty extends Stream[Nothing] {
  override val isEmpty = true
}

case class Cons[+A](hdFn: () => A, tlFn: () => Stream[A]) extends Stream[A] {
  override val isEmpty = false
}


object Stream {
  def apply[A](hd: => A, tl: => Stream[A]): Stream[A] = {
    lazy val lzyHd = hd // lazy is fein, weil da cached die Werte => performance go brrrr
    lazy val lzyTl = tl
    Cons(() => lzyHd, () => lzyTl)
  }

  def iterate[A](initial: A, nextFn: A => A): Stream[A] = Stream(initial, iterate(nextFn(initial), nextFn))

  def from[A](lst: List[A]): Stream[A] =
    lst match {
      case List() => Empty
      case x::xs => Stream(x, from(xs))
    }
}
