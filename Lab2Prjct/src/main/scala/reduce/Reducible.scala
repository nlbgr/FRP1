package reduce

import Monoid.{intPlusMonoid, *}
import tree.{BinNode, BinTree, EmptyTree}

trait Reducible[A] {
  def reduceMap[B](mapper: A => B)(using monoid: Monoid[B]): B
  def reduce(using monoid: Monoid[A]): A = reduceMap(a => a)

  def asList: List[A] = reduceMap(List(_))(using listMonoid)
  def asSet: Set[A] = reduceMap(Set(_))(using setMonoid)
  def count: Int = reduceMap(_ => 1)
  def sum(fn: A => Int): Int = reduceMap(fn)
}

object Reducible {

  def apply[A](as: Iterable[A]): Reducible[A] = new Reducible[A] {
    override def reduceMap[B](mapper: A => B)(using monoid: Monoid[B]): B = {
      var r = monoid.zero
      for (a <- as) {
        val b = mapper(a)
        r = monoid.op(r, b)
      }
      r
    }
  }

  def apply[A](tree: BinTree[A]): Reducible[A] = new Reducible[A] {
    override def reduceMap[B](mapper: A => B)(using monoid: Monoid[B]): B =
      tree match
        case EmptyTree => monoid.zero
        case BinNode(elem, left, right) => {
          val b = mapper(elem)
          val lr = Reducible(left).reduceMap(mapper)
          val rr = Reducible(right).reduceMap(mapper)
          monoid.op(lr, monoid.op(b, rr))
        }  
  }
}

