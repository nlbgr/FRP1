package reduce

import java.util.concurrent.{ForkJoinPool, RecursiveTask}

trait ParReducible[A] extends Reducible[A] {
  val THRESHOLD = 7

  def size : Int
  def split: (ParReducible[A], ParReducible[A])

  def parReduceMap[B](mapper : A => B)(using monoid: Monoid[B]) : B = {

    class Task(parAs: ParReducible[A]) extends RecursiveTask[B] {
      override def compute(): B = {
        if (parAs.size <= THRESHOLD) then {
          parAs.reduceMap(mapper)
        } else {
          val (as1, as2) = parAs.split
          val task1 = new Task(as1)
          val task2 = new Task(as2)
          task1.fork()
          task2.fork()
          println(s"JOIN: $as1 - $as2")
          monoid.op(task1.join(), task2.join())
        }
      }
    }

    val task = new Task(this)
    ForkJoinPool.commonPool.invoke(task)
  }

  def parReduce(monoid: Monoid[A]) : A = parReduceMap(a => a)(using monoid)
}

object ParReducible {

  def apply[A](as: Iterable[A]) : ParReducible[A] =
    new ParReducible[A] {

      def size = as.size

      def split: (ParReducible[A], ParReducible[A]) = {
        println(s"SPLIT: $this")
        val (as1, as2) = as.splitAt(as.size / 2)
        (apply(as1), apply(as2))
      }

      override def reduceMap[B](mapper: A => B)(using monoid: Monoid[B]): B = {
        println(s"SEQ: $this")
        var b = monoid.zero
        for (a <- as) {
          b = monoid.op(b, mapper(a))
        }
        b
      }

      override def toString: String = as.toString()

    }

}

object ParReduceDemo {

  def main(args: Array[String]): Unit = {

    val ns = (1 to 100).toList

   val parReducible : ParReducible[Int] = ParReducible(ns)

    val sump = parReducible.parReduceMap( i => i )(using Monoid.intPlusMonoid)
    println(s"sump = $sump")

  }

}
