package reduce

import Monoid.{intPlusMonoid, listMonoid, setMonoid, *}
import tree.{BinNode, BinTree, EmptyTree}

trait Reducible[A] {
  def reduceMap[B](mapper: A => B)(using monoid: Monoid[B]): B
  def reduce(using monoid: Monoid[A]): A = reduceMap(a => a)

  // TODO: Task 6.3 Methods in Reducible
  def asList: List[A] = reduceMap(a => List(a))(using listMonoid)
  def asSet: Set[A] = reduceMap(a => Set(a)) (using setMonoid)
  def count: Int = reduceMap(a => 1) (using intPlusMonoid)
  def sum(fn: A => Int): Int = reduceMap(a => fn(a)) (using intPlusMonoid)
}

object Reducible {

  def apply[A](as: Iterable[A]): Reducible[A] =
    new Reducible[A] {
      def reduceMap[B](mapper: A => B)(using monoid: Monoid[B]): B = {
        var r = monoid.zero
        for (a <- as) {
          r = monoid.op(r, mapper(a))
        }
        r
      }

    }

  def apply[A](tree: BinTree[A]): Reducible[A] =
    new Reducible[A] {
      def reduceMap[B](mapper: A => B)(using monoid: Monoid[B]): B = {
        tree match {
          case EmptyTree => monoid.zero
          case BinNode(elem, left, right) => {
            val lr = Reducible(left).reduceMap(mapper)
            val rr = Reducible(right).reduceMap(mapper)
            monoid.op(lr, monoid.op(mapper(elem), rr))
          }
        }
      }

      }

}

