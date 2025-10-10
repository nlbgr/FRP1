package ex3_graph

import scala.collection.immutable.Queue

trait Graph {
  type N  // abstract type for nodes

  val nodes: Set[N]
  val edges: Set[(N, N)]

  def successors(node: N) : Set[N] = edges.filter((s, _) => s == node).map((_, e) => e)

  def computeDists(start: N): Map[N, Int] = {
    def distsRec(queue: Queue[N], result: Map[N, Int], visited : Set[N]) : Map[N, Int] = {
      if (queue.isEmpty) result
      else {
        val node = queue.head

        val succList = successors(node).removedAll(visited).removedAll(queue)

        val nextVisited = visited + node
        val nextQueue = queue.tail.appendedAll(succList)
        val nextResult = result ++ succList.map(succ => (succ, result(node) + 1))
        distsRec(nextQueue, nextResult, nextVisited)
      }
    }

    distsRec(Queue(start), Map(start -> 0), Set())
  }

  def computePaths(start: N): Map[N, List[N]] = {
    def pathsRec(queue: Queue[N], result: Map[N, List[N]], visited: Set[N]): Map[N, List[N]] = {
      if (queue.isEmpty) result
      else {
        val node = queue.head

        val succList = successors(node).removedAll(visited).removedAll(queue)

        val nextVisited = visited + node
        val nextQueue = queue.tail.appendedAll(succList)
        val nextResult = result ++ succList.map(succ => (succ, succ :: result(node)))
        pathsRec(nextQueue, nextResult, nextVisited)
      }
    }

    pathsRec(Queue(start), Map(start -> List(start)), Set()).map((k, v) => (k, v.reverse))
  }

  def computeValues[R](start: N, startValue: R, resultFn: (N, R) => R): Map[N, R] = {
    def copmuteRec(queue: Queue[N], result: Map[N, R], visited: Set[N]): Map[N, R] = {
      if (queue.isEmpty) result
      else {
        val node = queue.head

        val succList = successors(node).removedAll(visited).removedAll(queue)

        val nextVisited = visited + node
        val nextQueue = queue.tail.appendedAll(succList)
        val nextResult = result ++ succList.map(succ => (succ, resultFn(succ, result(node))))
        copmuteRec(nextQueue, nextResult, nextVisited)
      }
    }

    copmuteRec(Queue(start), Map(start -> startValue), Set())
  }

  def computeDistsG(start: N): Map[N, Int] = computeValues(start, 0, (_, result) => result + 1)


  def computePathsG(start: N): Map[N, List[N]] = computeValues(start, List(start), (succ, result) => succ :: result).map((k, v) => (k, v.reverse))

}

trait IntGraph extends Graph {
  type N = Int
}


