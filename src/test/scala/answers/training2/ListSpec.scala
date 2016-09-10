package answers.training2

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks._
import org.scalatest.{FreeSpec, Matchers}

/**
  * Created by Wayne on 2016/4/29.
  */
class ListSpec extends FreeSpec with Matchers {

  type StdList[A] = scala.collection.immutable.List[A]

  sealed trait List[+A]

  case object Nil extends List[Nothing]

  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  object List {
    def apply[A](as: A*): List[A] = {
      def go(seq: Seq[A], list: List[A]): List[A] = {
        if (seq.isEmpty) list
        else go(seq.tail, Cons(seq.head, list))
      }
      go(as, Nil)
    }

    def sum(ints: List[Int]): Int = ints match {
      case Nil => 0
      case Cons(x, xs) => x + sum(xs)
    }

    def product(ds: List[Double]): Double = ds match {
      case Nil => 1.0
      case Cons(0.0, _) => 0.0
      case Cons(x, xs) => x * product(xs)
    }

    def drop[A](list: List[A], n: Int): List[A] =
      list match {
        case Nil => Nil
        case Cons(_, t) if n > 0 => drop(t, n - 1)
        case _ if n == 0 => list
      }

    def dropWhile[A](list: List[A])(f: A => Boolean): List[A] =
      list match {
        case Nil => Nil
        case Cons(h, t) if f(h) => dropWhile(t)(f)
        case _ => list
      }

    def take[A](list: List[A], n: Int): List[A] = {
      def go(n: Int, acc: List[A], l: List[A]): List[A] = {
        if (n <= 0) acc
        else l match {
          case Nil => acc
          case Cons(h, t) => go(n - 1, Cons(h, acc), t)
        }
      }
      reverse(go(n, Nil, list))
    }


    def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B =
      as match {
        case Nil => z
        case Cons(x, xs) => f(x, foldRight(xs, z)(f))
      }

    def foldLeft[A, B](as: List[A], z: B)(f: (B, A) => B): B =
      as match {
        case Nil => z
        case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
      }

    def length[A](list: List[A]): Int = foldLeft(list, 0)((acc, _) => acc + 1)

    def sumViaFoldLeft(ints: List[Int]): Int = foldLeft(ints, 0)(_ + _)

    def productViaFoldLeft(ds: List[Double]): Double = foldLeft(ds, 1.0)(_ * _)

    //练习五
    def reverse[A](list: List[A]): List[A] = foldLeft(list, Nil: List[A])((acc, h) => Cons(h, acc))

    //练习六
    def foldRightViaFoldLeft[A, B](list: List[A], z: B)(f: (A, B) => B): B =
      foldLeft(reverse(list), z)((acc, h) => f(h, acc))

    //练习七
    def append[A](a1: List[A], a2: List[A]): List[A] =
      foldRightViaFoldLeft(a1, a2)((elemOfSecond, firstList) => Cons(elemOfSecond, firstList))

    //练习八
    def concat[A](l: List[List[A]]): List[A] =
      foldRightViaFoldLeft(l, Nil: List[A])(append)

    //练习九
    def map[A, B](list: List[A])(f: A => B): List[B] =
      foldRightViaFoldLeft(list, Nil: List[B])((a, b) => Cons(f(a), b))

    //练习十
    def filter[A](list: List[A])(f: A => Boolean): List[A] =
      foldRightViaFoldLeft(list, Nil: List[A])((a, b) => if (f(a)) Cons(a, b) else b)

    //练习十一
    def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = concat(map(list)(f))

    //练习十二
    def mapViaFlatMap[A, B](list: List[A])(f: A => B): List[B] =
      flatMap(list)(a => List(f(a)))

    //练习十三
    def filterViaFlatMap[A](list: List[A])(f: A => Boolean): List[A] =
      flatMap(list)(a => if(f(a)) List(a) else Nil)


  }


  import List._

  def stdToList[A](list: StdList[A]):List[A] = List(list: _*)

  "模式匹配" - {
    "应该匹配第一个符合要求的模式，并返回之后的表达式的值" in {
      val i = List(1, 2, 3, 4, 5) match {
        case Cons(x, Cons(2, Cons(4, _))) => x
        case Nil => 42
        case Cons(x, Cons(y, Cons(3, Cons(4, _)))) =>
          x + y
        case Cons(h, t) => h + sum(t)
        case _ => 101
      }

      println(i)
    }
  }

  "drop和dropWhile函数" - {
    val ns = Gen.choose(0, 100)
    val lists = for {
      n <- ns
      seq <- Gen.listOf(ns)
    } yield stdToList(seq)


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

    val seqIntsGen = Gen.listOf(Gen.choose(0, 1000)).map(stdToList)
    val seqDoublesGen = Gen.listOf(Gen.choose(0.0, 100.0)).map(stdToList)

    "应该与sum和product计算出来的一致" in {
      forAll(seqIntsGen) { list =>
          sum(list) shouldBe sumViaFoldLeft(list)
        }

      forAll(seqDoublesGen) { list =>
          product(list) / productViaFoldLeft(list) shouldBe 1.0 +- 0.00000001
        }
    }
  }

  "reverse函数" - {
    "对Nil应用的时候，获得的还是Nil" in {
      reverse(Nil) shouldBe Nil
    }

    "对只有一个元素的list应用之后，获得的还是list" in {
      val oneElemList = Gen.listOfN(1, Gen.choose(-100, 99)).map(stdToList)
      forAll(oneElemList) { list =>
        reverse(list) shouldBe list
      }

    }

    "对非空list应用一次之后，与list不相等，两次之后产生的列表与list相等" in {
      val listGen: Gen[List[Int]] =
        Gen.listOf(Gen.choose(-100, 99)).map(stdToList)
          .filter {
            case Nil => false
            case Cons(_, Nil) => false
            case _ => true
          }
      forAll(listGen) { list =>
        reverse(list) should not be list
        reverse(reverse(list)) shouldBe list
      }
    }
  }

  "foldRightViaFoldLeft函数" - {
    def lists(lengthGen: Gen[Int]): Gen[List[Int]] = for {
      n <- lengthGen
      list <- Gen.listOfN(n, Gen.choose(0, 100000))
    } yield stdToList(list)

    "在list长度较小的时候，产生和foldRight相等的值" in {
      val shortLists = lists(Gen.choose(0, 100))
      forAll(shortLists) { list =>
        foldRight(list, 0)(_ + _) shouldBe foldRightViaFoldLeft(list, 0)(_ + _)
      }

    }

    "在list长度较大的时候，foldRight会抛出StackOverflowError，而foldRightViaFoldLeft不会抛出异常" in {
      val longLists = lists(Gen.choose(200000, 300000))
      forAll(longLists) { list =>
        a[StackOverflowError] shouldBe thrownBy(foldRight(list, 0)(_ + _))
        foldRightViaFoldLeft(list, 0)(_ + _) shouldBe foldLeft(list, 0)(_ + _)
      }
    }
  }

  "append函数会将第二个参数列表添加到第一个参数列表的末尾，产生的新列表" - {
    val lists = Gen.listOf(Gen.choose(0, 100)).map(stdToList)
    "新列表的长度应该是两个列表长度的和。新列表drop第一个列表长度的元素后，得到的列表应该与第二个列表相等" in {
      forAll(lists, lists) { (f, s) =>
        val appendList = append(f, s)
        val n1 = List.length(f)
        val n2 = List.length(s)
        List.length(appendList) shouldBe n1 + n2
        drop(appendList, n1) shouldBe s
        take(appendList, n1) shouldBe f
      }
    }
  }

  "concat函数" - {
    val listsOfLists = Gen.listOf(Gen.listOf(Gen.choose(0, 100)))
    "应该与标准库中的flatten函数产生一样的效果" in {
      forAll(listsOfLists) { lol =>
        val clol = stdToList(lol.map(stdToList))
        val flattenedLol = stdToList(lol.flatten)
        concat(clol) shouldBe flattenedLol
      }
    }
  }


  "map函数" - {
    "应该与标准库中的map函数产生一样的效果" in {
      val f1 = (x: Int) => x + 1
      val f2 = (x: Int) => x - 2
      val f3 = (x: Int) => x * 3

      forAll(Gen.listOf(Gen.choose(0, 100))) {stdList =>
        val list = stdToList(stdList)
        map(list)(f1) shouldBe stdToList(stdList.map(f1))
        map(list)(f2) shouldBe stdToList(stdList.map(f2))
        map(list)(f3) shouldBe stdToList(stdList.map(f3))
      }
    }
  }

  "filter函数" - {
    "应该与标准库中的filter函数产生一样的效果" in {
      val even = (x:Int) => x % 2 == 0
      val odd = (x:Int) => x % 2 != 0

      forAll(Gen.listOf(Gen.choose(0, 100))) {stdList =>
        val list = stdToList(stdList)
        filter(list)(even) shouldBe stdToList(stdList.filter(even))
        filter(list)(odd) shouldBe stdToList(stdList.filter(odd))
      }
    }
  }


}
