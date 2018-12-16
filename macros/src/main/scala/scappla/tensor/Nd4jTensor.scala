package scappla.tensor

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms

object Nd4jTensor {

  implicit val ops: DataOps[INDArray] = new DataOps[INDArray] {

    override def zeros(shape: Int*): INDArray =
      Nd4j.create(shape: _*)

    // elementwise operations

    override def plus(a: INDArray, b: INDArray): INDArray =
      a.add(b)

    override def minus(a: INDArray, b: INDArray): INDArray =
      a.sub(b)

    override def times(a: INDArray, b: INDArray): INDArray =
      a.mul(b)

    override def div(a: INDArray, b: INDArray): INDArray =
      a.div(b)

    override def negate(a: INDArray): INDArray =
      a.neg()

    override def log(a: INDArray): INDArray =
      Transforms.log(a)

    override def exp(a: INDArray): INDArray =
      Transforms.exp(a)

    // reshaping operations

    override def sum(a: INDArray, dim: Int, shape: Int*): INDArray = {
      val oldShape = shape.toSeq
      val result = a.sum(dim)
      val newShape = oldShape.take(dim) ++ oldShape.drop(dim + 1)
      val finalResult = result.reshape(newShape.toArray)
      finalResult
    }

    override def broadcast(a: INDArray, dimIndex: Int, dimSize: Int, shape: Int*): INDArray = {
      val oldShape = shape.toSeq
      val newTmp: Seq[Int] = (oldShape.take(dimIndex) :+ 1) ++ oldShape.drop(dimIndex)
      val reshaped = a.reshape(newTmp.toArray)
      val newShape: Seq[Int] = (oldShape.take(dimIndex) :+ dimSize) ++ oldShape.drop(dimIndex)
      reshaped.broadcast(newShape.map { _.toLong }.toArray: _*)
    }
  }

}

