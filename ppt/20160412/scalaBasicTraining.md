title: Scala基础培训
speaker: Wayne Wang
transition: slide3

[slide]

# Scala基础培训

## 语言简介以及基本注意事项

[slide]

## Scala的起源

* 创始人Martin Odersky {:&.moveIn}
  * 瑞士洛桑联邦理工学院计算机教授 {:&.moveIn}
  * 专注于代码分析和编程语言
  * Java泛型的设计者
  * 现代版本javac的作者
  * 将函数式语言引入JVM平台的先行者
* 结合面向对象与函数式编程
  * 拒绝强制纯函数式 {:&.moveIn}
  * 拥抱Java
  * 基于面向对象，拥抱函数式编程

[slide]

## 从Java转Scala的过程

* 暂时可以用Scala写Java一样的代码 {:&.moveIn}
* 注意在合适的地方使用Scala特性
* 逐渐引入函数式编程的思想
* 善用Scala原生的并发机制

[slide]

## Scala有哪些特性使得它值得青睐？
-------

1. 与Java无缝互操作 {:&.moveIn}
2. 类型推断
3. 并发与分布式
4. 特质
5. 模式匹配
6. 函数式特性

[slide]
## 这些特性能给我们带来什么？

[slide]

## 与Java无缝互操作

* 都是JVM上的class文件 {:&.moveIn}
* Scala可以轻松调用现有的Java库
* 用sbt替代maven来引入Java类库
* 这意味着Java的生态圈可以轻松为Scala所用
* 对Scala不熟的阶段，可以用Scala写Java一样的代码，提供缓冲过程。

[slide]

## 类型推断

* Java无论何时都需要显示地标注类型： {:&.moveIn}
  *  ```java
     String s = "Hello, world!"
  ``` 
* Scala则只需要在恰当的时候标注类型即可: 
  * ```scala
  val s = "Hello, world!"
  ```
* 尤其在调用函数的时候，格外方便。
<div class="columns-2">
    <pre><code class="java">
String hello(String name) {
    return "Hello " + name;
}
String greeting = hello("Wayne");
    </code></pre>
    <pre><code class="scala">
def hello(name:String):String = 
    s"Hello $name"
val greeting = hello("Wayne")
    </code></pre>
</div>

* 但是在难以理解的地方标注上类型是良好的习惯

[slide]

##并发与分布式

* 默认的Fork-Join Pool {:&.moveIn}
  * 只需要  {:&.moveIn}
  ```scala 
  import scala.concurrent.ExecutionContext.Implicits.global
  ``` 
  
  * 线程数少，默认核心数相等数量的线程，最大程度利用CPU 
  * Work stealing机制
* 跟Java相比非常方便的`Future`类型来提交异步任务 
  * ```scala 
  Future { doSomethingExpensive() } 
  ```
* 异步任务之间的结合非常方便
```scala
val x = Future { someExpensiveComputation() }
val y = Future { someOtherExpensiveComputation() }
val z = for (a <- x; b <- y) yield a*b
for (c <- z) println("Result: " + c)
println("Meanwhile, the main thread goes on!")
```
* **注意** twitter的future的机制和scala的不一样 

[slide]

## 特质
  
* 特质提供了mixin（混入）风格的继承 {:&.moveIn}
* 可以把特质看成是Java的多继承实现 
* 可以把特质看作是可以实现default方法的Java接口
* 可以利用特质通过cake pattern来实现依赖注入
* **注意** 特质提供了灵活的用法，但是要避免多继承的坑，比如臭名昭著的棱形继承

[slide]

## 模式匹配

* 更加强大的switch {:&.moveIn}
```scala
// Define a set of case classes for representing binary trees.
sealed abstract class Tree
case class Node(elem: Int, left: Tree, right: Tree) extends Tree
case object Leaf extends Tree
// Return the in-order traversal sequence of a given tree.
def inOrder(t: Tree): List[Int] = t match {
  case Node(e, l, r) => inOrder(l) ::: List(e) ::: inOrder(r)
  case Leaf          => List()
}
```
* 守卫
```scala
case Node(e, l, r) if e > 0 => doSomeThing()
```

[slide]

## 模式匹配

* 更加方便的抽取 {:&.moveIn}
  <div class="columns-2">
  <pre><code class="java">public class Customer {
      //手写所有字段
      private Long ID;
      private String name;
      
      public Long getID() {
          return ID;
      }
  
      public void setID(Long ID) {
          this.ID = ID;
      }
  
      public String getName() {
          return name;
      }
  
      public void setName(String name) {
          this.name = name;
      }
  }
  
  Customer customer = new Customer(...);
  Long id = customer.getID();
  String name = customer.getName();
  </code></pre>
  <pre><code class="scala">case class Customer(
      ID:Long, 
      name:String, 
      age:Int)
  val customer = aGetCustomerMethod()
  val Customer(id, name, _) = customer
  </code></pre>
  </div>
  
[slide]

## 高阶函数

* 随时随地可以声明并使用函数： {:&.moveIn}
  * fibonacci数 {:&.moveIn}
  ```scala
    def fibonacci(n:Int):Int = {
      def go(current:Int, next:Int, n:Int):Int = {
       if(n == 0) current
       else go(next, current + next, n - 1)
      }
      go(0,1, n)
    }
   ```
* 函数可以作为参数：
  * 集合的map操作： {:&.moveIn}
  ```scala
  def f(a:Int):Int = a + 5
  val list = List(1,2,3,4)
  list map f   //或者直接使用匿名函数： list map (_ + 5)
  ```
* 函数可以成为返回值：
  * 柯里化 {:&.moveIn}
  ```scala
def plus(a:Int, b:Int):Int = a + b
def curry[A,B,C](f: (A , B) => C): A => B => C = a => b => f(a, b)
def f = curry(plus)
def plus5 = f(5)
plus5(5)  //得到10
  ```

[slide]

## 函数式编程

在Scala中，函数成为了一等公民。函数可以随时定义、当作参数传入函数、作为返回值返回。这使得利用Scala来进行函数式编程成为了可能。 {:&.moveIn}

[slide]

## 函数式编程
* 定义： {:&.moveIn}
  * 用纯函数来构建程序 {:&.moveIn}
  * 一个函数是纯的，如果这个函数没有副作用
  * 如果一个函数在执行的过程中产生了除了根据给定输入计算结果并返回以外的可以被观测到的影响，这种影响就叫做副作用。这种副作用包括：
   * 函数外的某个变量被修改了； {:&.moveIn}
   * 函数外的某个对象的某个字段被设置了；
   * 函数抛出了异常，或者因为某个错误而中止了；
   * 函数向终端做了输出或者读入了用户的输入；
   * 函数对某个文件做了读取或者写入的操作；
   * 函数在屏幕上绘制了图形；
   * 以及其他任何在函数外可以被观测到的作用
   
[slide]

## 函数式编程
   
* 纯函数式编程太难 {:&.moveIn}
  * 拒绝可变量 {:&.moveIn}
  * 使用monad处理IO
  * 学习成本略高
  
* 纯函数式编程优点 {:&.moveIn}
  * 模块清晰 {:&.moveIn}
  * 易于复用
  * 没有副作用，易于并发
  
[slide]

## 函数式编程实践

>通常，我们需要利用一定的规则来将函数的副作用尽可能推迟到程序的最外层。实践中，我们实现函数式编程，说的就是构建一个内部是由纯函数组成的核心，然后最外部薄薄的一层处理副作用的程序。

{:&.moveIn}

[slide]

## 函数式编程的一些原则

* 拒绝null，使用Option {:&.moveIn}
  * 如果一个值可能是null，则将其放入Option中 {:&.moveIn}
  ```scala
  val maybeCustomer:Option[Custumer] = Option(getCustomerFromJavaCode())
  ```
  
* 使用不可变的函数式数据结构 {:&.moveIn}
  * 利用case class构建不可变的函数式数据结构 {:&.moveIn}
  
* 将异常当作值来返回 {:&.moveIn}
  * Try {:&.moveIn}
  * Either
  
* 熟练使用Scala标准集合库的用法 {:&.moveIn}
  * map、flatMap、find、exists、filter、fold、reduce {:&.moveIn}
  
[slide]

## 代码演示：

{:&.moveIn}
```scala
private var cache: Option[Boolean] = None
  
def getIsValid:Boolean = {
    cache = cache match {
      case v if v.isEmpty =>
        val p = this.productIterator
        var b = true
        while (b && p.hasNext) {
          p.next() match {
            case field:CheckField if field.isValide != 1 =>
                println(field); b=false; isValid=false
            case _ =>
          }
        }
        Some(b)
      case _ => cache
    }
    cache.getOrElse(false)
  }
```

[slide]

## 可以改进的地方

* 并没有利用特别的模式匹配功能。 {:&.moveIn}
```scala
cache match {
    case v if v.isEmpty =>
        ...
    case _ => cache
```
等价于
```scala
if(cache.isEmpty) {... } else {}
```
而后者更易于理解

[slide]

## 可以改进的地方

* Scala中，对iterator进行操作的时候，应该更加倾向于map。
```scala
val p = this.productIterator
var b = true
while (b && p.hasNext) {
    p.next() match {
        case field:CheckField if field.isValide != 1 =>
            println(field); b=false; isValid=false
        case _ =>
    }
}
```

* 当然，根据对代码的理解，这里不是要做映射操作，而是判断是否存在无效的字段，则可以改成如下形式：
```scala
this.productIterator.exists {
        case field:CheckField if field.isValide != 1 => 
            println(field)
            true
        case _ => false
    }
```

[slide]

## 可以改进的地方

* 最终改成如下形式：

```scala
private var cache: Option[Boolean] = None
  
def getIsValid:Boolean = {
    cache = cache.orElse (
      Some {
        !this.productIterator.exists {
          case field: CheckField if field.isValide != 1 =>
            println(field)
            isValid = false
            true
          case _ => false
        }
      }
    )
    cache.getOrElse(false)
}
```

[slide]

## future相关的代码演示

[slide]

```scala
    for {
      users <- data.user()
      brands <- data.brand()
      products <- data.product()
      warehouses <- data.warehouse()
    } yield {
      try {
        taskProgress.setProgress(0, "解析数据", 0.1)
        log.debug("criterion 解析数据 ...")
        val tempParam = TempParam(in, user, attr7, 0)
        val result = tempAttr.toBean(tempParam)
        val analyseParam = AnalyseParam(users, brands, warehouses, products)
        analyseAttr.init(analyseParam)
        log.debug("criterion 验证数据 ...")
        taskProgress.setProgress(0, "验证数据", 0.2)
        try {
          result.par.foreach(p => analyseAttr.work(p))
        } catch {
          case e: Exception => e.printStackTrace()
        }
        analyseAttr.isValid match {
          case true =>
            taskProgress.setProgress(0, "保存数据", 0.4)
            val datas = result.map(p=>p.toInventory)
            new InventoryDao().bulkCreate(datas)
            taskProgress.setProgress(0, "刷新索引", 0.8)
          case false =>
            saveErrorData(result, guid)
            taskProgress.setProgress(2, "验证失败", 1)
        }
      } catch {
        case e: Exception => e.printStackTrace()
      }
    }


```

[slide]

* 异常处理和业务代码最好分离，不要混在在一起。
* Future中不需要try catch，用onFailure、recover、recoverWith(twitter的是rescue)来处理异常

[slide]

* 目测代码的目的是获得一个result和analyseParam，然后对这两个处理。修改的话，可以：
  * 获得一个Future[ResultType, Param]
  * 创建一个函数f:(ResultType, Param) => Unit
  * 
  ```scala
  future.foreach(f).onFailure{ case e:Exception => e.printStackTrace() }
  ```

[slide]

```scala
   val f = for {
      users <- data.user()
      brands <- data.brand()
      products <- data.product()
      warehouses <- data.warehouse()
    } yield {
      taskProgress.setProgress(0, "解析数据", 0.1)
      log.debug("criterion 解析数据 ...")
      val tempParam = TempParam(in, user, attr7, 0)
      val result = tempAttr.toBean(tempParam)
      val analyseParam = AnalyseParam(users, brands, warehouses, products)
      analyseAttr.init(analyseParam)
      log.debug("criterion 验证数据 ...")
      taskProgress.setProgress(0, "验证数据", 0.2)
      result.par.foreach(p => analyseAttr.work(p))
      analyseAttr.isValid match {
        case true =>
          taskProgress.setProgress(0, "保存数据", 0.4)
          val datas = result.map(p => p.toInventory)
          new InventoryDao().bulkCreate(datas)
          taskProgress.setProgress(0, "刷新索引", 0.8)
        case false =>
          saveErrorData(result, guid)
          taskProgress.setProgress(2, "验证失败", 1)
      }
    }
    f onFailure {
      case e:Exception => e.printStackTrace()
    }
```

[slide]

* For是map、flatMap和withFilter的语法糖，简单操作不需要for，直接map即可
```scala
    val data = TaskController.getTaskProgressInfo(request.getParam("batch_code", ""))
    for {
      r <- data
    } yield response.json(r)
```
可以写成:
```scala
data map (response.json(_))
```

[slide]

* 进行异步操作的时候，如果后一项Future对前一项Future没有依赖，则最好在for之前即执行。

<video width="640" height="480" controls="controls">
  <source src="/images/responsiveness.mp4" type="video/mp4" />
</video>

[slide]

## 推荐的学习资料

* 书 {:&.moveIn}
  * 基本语法：《快学Scala》，《Programming in Scala》 {:&.moveIn}
  * 函数式编程：《Functional Programming in Scala》
  * 并发编程： 《Java Concurrency in Practice》， 《Learning Concurrent Programming in scala》
  * 响应式设计： 《Reactive Web Application》，《Reactive Design Patterns》

* 课程 {:&.moveIn}
  * [Functional Programming Principles in Scala](https://www.coursera.org/course/progfun) {:&.moveIn}
  * [Principles of Reactive Programming](https://www.coursera.org/course/reactive)
  * [Parallel programming](https://www.coursera.org/learn/parprog1)

[slide]

## Thanks

[slide]

## Q&A
