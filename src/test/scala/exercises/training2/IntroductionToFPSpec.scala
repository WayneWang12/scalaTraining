package exercises.training2

import org.scalatest.{FreeSpec, Matchers}


/**
  * Created by Wayne on 2016/4/29.
  */
class IntroductionToFPSpec extends FreeSpec with Matchers {

  val list = List(1, 2, 3, 4)

  "一个循环" - {
    "应该清晰地表达自己的意图" in {
      var listPlus1: List[Int] = List()
      val iterator = list.iterator
      while (iterator.hasNext) {
        listPlus1 = (iterator.next() + 1) :: listPlus1
      }

      listPlus1 shouldBe list.map(_ + 1)
    }
  }

  "exist函数" - {
    "应该清晰明确地表达过滤条件" in {
      def existEven(list: List[Int]): Boolean = {
        val iterator = list.iterator
        while (iterator.hasNext) {
          if (iterator.next() % 2 == 0) return true
        }
        false
      }

      def existEvenFS(list: List[Int]): Boolean = list.exists(_ % 2 == 0)

      val oddList = List(1, 3, 5, 7)
      existEven(list) shouldBe true
      existEven(list) shouldBe existEvenFS(list)
      existEven(oddList) shouldBe false
      existEven(oddList) shouldBe existEvenFS(oddList)
    }
  }

  "一个引用透明的表达式" - {
    "用该表达式的结果替换之后，程序的意义不会改变" in {
      val x = "Hello, world"
      val r1 = x.reverse
      val r2 = r1
      val r3 = x.reverse
      r2 shouldBe r3
      //用hello world替换x以后，

    }
  }

  "一个非应用透明的表达式" - {
    "在用改表达式的结果替换之后，程序的意义改变了" in {
      val x = new StringBuilder("Hello")
      val y1 = x.toString()

      val y2 = x.toString()
      y1 shouldBe y2
      val r1 = x.append("world").toString()
      val r2 = r1
      val r3 = x.append("world").toString()
      r2 should not be r3
    }
  }

  "代换模型" - {
    "在接受了一个引用透明的参数之后进行代换推导得到的值应该与函数计算结果相等" in {
      def f(a: Int): Int = sumOfSquare(a + 1, a * 2)
      def sumOfSquare(a: Int, b: Int): Int = square(a) + square(b)
      def square(a: Int): Int = a * a

      f(5)
      sumOfSquare(5 + 1, 5 * 2)
      sumOfSquare(6, 10)
      square(6) + square(10)
      (6 * 6) + (10 * 10)
      36 + 100
      136 shouldBe f(5)
    }
  }

  def factorial(n: Int): Int = {
    def go(n: Int, acc: Int): Int =
      if (n <= 0) acc
      else go(n - 1, n * acc)
    go(n, 1)
  }

  "一个普通递归" - {
    "在栈太深的情况下，会抛出StackOverflow的异常" in {
      def sumOfN(n: Int): Int = {
        if (n == 0) 0
        else n + sumOfN(n - 1)
      }


      def sumOfNTailRec(n: Int): Int = {
        def go(n: Int, acc: Int): Int = {
          if (n <= 0) acc
          else go(n - 1, n + acc)
        }
        go(n, 0)
      }

      def quickSum(n: Int) = n * (n + 1) / 2

      sumOfN(10) shouldBe quickSum(10)
      sumOfN(100) shouldBe quickSum(100)
      a[StackOverflowError] should be thrownBy sumOfN(100000)
      sumOfNTailRec(10) shouldBe quickSum(10)
      sumOfNTailRec(100) shouldBe quickSum(100)
      sumOfNTailRec(100000) shouldBe quickSum(100000)
    }
  }

  //练习一
    def fib(n:Int):BigDecimal = {
    if (n == 0 || n == 1 ) n
    else fib(n - 1) + fib(n -2 )
  }

  "一个尾递归的fib函数" - {
    "应该在栈深度很大的时候也不会StackOverflow" in {
      fib(0) shouldBe 0
      fib(1) shouldBe 1
      fib(7) shouldBe 13
      fib(8) shouldBe 21
      fib(9) shouldBe 34
      fib(50) shouldBe 12586269025l
      fib(1000) shouldBe BigDecimal("43466557686937456435688527675040625802564660517371780402481729089536555417949051890403879840079255169295922593080322634775209689623239873322471161642996440906533187938298969649928516003704476137795166849228875")
    }
  }

  //练习二
  def isSorted[A](as: Array[A], ordered: (A, A) => Boolean): Boolean = ???

  "isSorted函数" - {
    "应该可以判断一个数组是否是排序好了的" in {
      val stringArray = Array("wayne", "is", "handsome")
      isSorted[String](stringArray, (a, b) => a < b) shouldBe false
      isSorted[String](stringArray, (a, b) => a > b) shouldBe true

      val increasingInts = Array(1, 2, 3, 4)
      val decreasingInts = Array(4, 3, 2, 1)
      val unorderedInts = Array(2, 1, 3, 4)

      def isIncreasingSorted(array: Array[Int]): Boolean = isSorted[Int](array, _ < _)
      def isDecreasingSorted(array: Array[Int]): Boolean = isSorted[Int](array, _ > _)


      isIncreasingSorted(increasingInts) shouldBe true
      isIncreasingSorted(decreasingInts) shouldBe false
      isDecreasingSorted(increasingInts) shouldBe false
      isDecreasingSorted(decreasingInts) shouldBe true
      isIncreasingSorted(unorderedInts) shouldBe false
      isDecreasingSorted(unorderedInts) shouldBe false
    }
  }

}
