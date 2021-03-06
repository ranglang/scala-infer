package scappla.distributions

import scappla.Functions.{log, sum}
import scappla._

case class Normal[D, S](
    mu: Expr[D],
    sigma: Expr[D]
)(implicit
    numE: InferField[D, S],
    numX: BaseField[D, S],
    logImpl: log.Apply[Expr[D], Expr[D]],
    sumImpl: sum.Apply[Expr[D]]
) extends DDistribution[D] {
  private val shape = numX.shapeOf(sigma.v)
  private val two = numE.fromInt(2, shape)

  override def sample(): Buffered[D] = {
    new Expr[D] with Buffered[D] {
      var refCount = 1

      val e: Expr[D] = Constant(numX.gaussian(shape))

      val r: Buffered[D] = {
        val sc = sigma.const
        ((mu + e * sigma / two) / (sc * sc)).buffer
      }

      override val v: D =
        (mu + e * sigma).v

      override def dv(x: D): Unit = {
        assert(refCount > 0)
        r.dv(x)
      }

      override def buffer: Buffered[D] = {
        refCount += 1
        this
      }

      override def complete(): Unit = {
        assert(refCount > 0)
        refCount -= 1
        if (refCount == 0) {
          r.complete()
        }
      }

      override def toString: String = {
        s"NormalSample(${hashCode()})"
      }
    }
  }

  override def observe(x: Expr[D]): Score = {
    val e = (x - mu) / sigma
    sum(-log(sigma) - e * e / two)
  }

  override def reparam_score(x: Expr[D]): Score = {
    val e = (x - mu.const) / sigma.const
    sum(-log(sigma).const - e * e / two)
  }
}
