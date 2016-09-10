package exercises.training2

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Wayne on 2016/4/29.
  */
class ListSpec extends FreeSpec with Matchers {

  sealed trait List[+A]

  case object Nil extends List[Nothing]

  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {
    def apply[A](as: A*): List[A] =
      if (as.isEmpty) Nil
      else Cons(as.head, apply(as.tail: _*))

    def sum(ints: List[Int]): Int = ints match {
      case Nil => 0
      case Cons(x, xs) => x + sum(xs)
    }

    def product(ds: List[Double]): Double = ds match {
      case Nil => 1.0
      case Cons(0.0, _) => 0.0
      case Cons(x, xs) => x * product(xs)
    }

    //练习三
    def drop[A](list: List[A], n: Int): List[A] = ???

    //练习三
    def dropWhile[A](list: List[A])(f: A => Boolean): List[A] = ???


    def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B =
      as match {
        case Nil => z
        case Cons(x, xs) => f(x, foldRight(xs, z)(f))
      }

    //练习四
    def foldLeft[A, B](as: List[A], z: B)(f: (B, A) => B): B = ???

    def length[A](list: List[A]): Int = foldLeft(list, 0)((acc, _) => acc + 1)

    def sumViaFoldLeft(ints: List[Int]): Int = foldLeft(ints, 0)(_ + _)

    def productViaFoldLeft(ds: List[Double]): Double = foldLeft(ds, 1.0)(_ * _)

    //练习五
    def reverse[A](list: List[A]): List[A] = ???

    //练习六
    def foldRightViaFoldLeft[A, B](list: List[A], z: B)(f: (A, B) => B): B = ???

    //练习七
    def append[A](a1: List[A], a2: List[A]): List[A] = ???

    //练习八
    def concat[A](l: List[List[A]]): List[A] = ???

    //练习九
    def map[A, B](list: List[A])(f: A => B): List[B] = ???

    //练习十
    def filter[A](list: List[A])(f: A => Boolean): List[A] = ???

    //练习十一
    def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = ???

    //练习十二
    def mapViaFlatMap[A, B](list: List[A])(f: A => B): List[B] = ???

    //练习十三
    def filterViaFlatMap[A](list: List[A])(f: A => Boolean): List[A] = ???

  }


  import List._

  "模式匹配" - {
    "应该匹配第一个符合要求的模式，并返回之后的表达式的值" in {
      val i = List(1, 2, 3, 4, 5) match {
        case Cons(x, Cons(2, Cons(4, _))) => x
        case Nil => 42
        case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
        case Cons(h, t) => h + sum(t)
        case _ => 101
      }

      println(i)
    }
  }

  "drop和dropWhile函数" - {
    val ns = Gen.choose(0, 100)
    val lists = for {
      seq <- Gen.listOf(ns)
    } yield List(seq: _*)


    "应该丢弃前n个参数，返回剩余的列表，如果列表长度小于n，则返回Nil" in {

      forAll(ns, lists) { (n, list) =>
        val length = List.length(list)
        val droppedList = drop(list, n)
        if (n >= length)
          droppedList shouldBe Nil
        else List.length(droppedList) shouldBe length - n
      }
    }

    "dropWhile函数应该丢弃所有符合条件的元素，直到遇到第一个不符合的元素为止" in {
      forAll(ns, lists) { (n, list) =>
        val droppedOddList = dropWhile(list)(_ % 2 != 0)
        droppedOddList match {
          case Cons(h, _) => h % 2 shouldBe 0
          case any => any shouldBe Nil
        }
      }
    }


  }

  "foldLeft的sum和product函数计算出来的结果" - {
    "应该与sum和product计算出来的一致" in {
      val seqIntsGen = Gen.listOf(Gen.choose(0, 1000)).map(_.toSeq)
      val seqDoublesGen = Gen.listOf(Gen.choose(0.0, 100.0)).map(_.toSeq)
      def getList[A](seq: Seq[A]): List[A] = List(seq: _*)
      forAll(seqIntsGen) { seq =>
        whenever(seq.length < 100) {
          val list = getList(seq)
          sum(list) shouldBe sumViaFoldLeft(list)
        }
      }

      forAll(seqDoublesGen) { (seq: Seq[Double]) =>
        whenever(seq.length < 100) {
          val list = getList(seq)
          product(list) / productViaFoldLeft(list) shouldBe 1.0 +- 0.00000001
        }

      }
    }
  }


}
