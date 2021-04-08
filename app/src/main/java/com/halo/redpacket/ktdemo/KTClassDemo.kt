package com.halo.redpacket.ktdemo

import android.view.View
import com.google.gson.Gson
import java.io.Serializable

/**
 * Author: zx
 * Date: 2020/11/30
 * Description:
 */
class KTClassDemo {

}

/**
 * 主构造函数可以省略constructor，无论在主构造函数中是否包含有参数。
 * 主构造函数的属性可以使用var、val修饰，次构造函数不能用他们进行修饰
 */
class KtFunDemo constructor(val str: String) {

    init {
        // 初始化块, Kotlin 的主构造函数可以借助初始化块，对代码进行初始化。
        println("test:$str 1")
    }

    init { // 多个初始化块
        println("$str 2")
    }

    /**
     * 次构造函数，同样使用constructor作为函数名，但不能省略函数名
     * 每个次构造函数需要委托给主构造函数，调用次构造函数时会先调用主构造函数以及初始化块。
     */
    constructor(str1: String, str2: String) : this(str1) {// 调用主构造函数以及初始化块
        println("test:$str1 $str2")
    }
}



data class User(var username: String? = null,
                var password: String? = null,
                var address: Address? = null
)

data class Address(var street: String? = null){

    var province: String?=null
    var city: String?=null

    override fun toString() = "province=$province,city=$city,street=$street"
}

/**
 * 嵌套类是指定义在某一个类内部的类，
 * 嵌套类不能够访问外部类的成员。除非嵌套类变成内部类。
 */
class Outter1 {
    val str: String = "this property is from outter1 class"

    class Nested {
        fun foo() = println("foo")
    }
}

/**
 * 内部类使用inner关键字标识，内部类能够访问外部类的成员。
 */
class Outter2 {
    val str: String = "this property is from outter2 class"

    inner class Inner {
        fun foo() = println("foo $str")
    }
}

fun main(args: Array<String>) {
    Outter1.Nested().foo()
    Outter2().Inner().foo()
    Singleton1.printlnHelloWorld()

    Student.changeMarks("B")
    println(Student.printMarks())

    val obj1 = Singleton2.instance
    val obj2 = Singleton2.instance
    println(obj1 === obj2)

    Singleton2.instance.printlnHelloWorld()

    val user1 = User("tom", "123456", Address("renming"))
    val user2 = user1.copy()
    println(user2)

    //Kotlin 中===比较的是内存地址。
    println(user1.address===user2.address) // 判断 data class 的 copy 是否为浅拷贝，如果二者的address指向的内存地址相同则为浅拷贝，反之为深拷贝
    val user3 = user1.copy("monica")
    println(user3)

    val user4 = user1.copy(password = "abcdef")
    println(user4)


    val gson = Gson()
    println(gson.toJson(PingMsg()))
    val map = mutableMapOf<String, String>()
    map.put("param1","tt")
    map.put("param2","qq")
    println(gson.toJson(AskMsg(body = map)))
}

/**Kotlin 中的枚举类需要使用enum和class两个关键字。*/
enum class Color constructor(var colorName: String, var value: Int) {
    RED("红色", 1),
    GREEN("绿色", 2),
    BLUE("蓝色", 3)
}

/**
 * 对象声明、对象表达式和伴生对象都用到了object关键字。
 *
 * 对象声明(Object declarations)
 * Kotlin 通过对象声明可以实现单例模式，这是 Kotlin 在语法层面上的支持。
 */
object Singleton1 {

    /**
     * 在 Java 中调用 Singleton1 的 printlnHelloWorld() 方法应该是这样的：
     * Singleton1.INSTANCE.printlnHelloWorld();
     */
    fun printlnHelloWorld() = println("hello world")
}

/**
 * 对象表达式(Object expressions)
 * 与 Java 的匿名内部类相比，它有以下特性：
支持实现多个接口
能够访问非 final 修饰的变量
 */
fun setListener(view: View) {
    view.setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {
            //
        }
    })
    //Convert to lambda
    view.setOnClickListener {
        //
    }
}

/**
 * 伴生对象(Companion Object)
 * Kotlin 没有static关键字，在 Kotlin 类中也不能拥有静态属性和静态方法。
 * 使用伴生对象是解决这个问题的方法之一。
 *
 * 如果 Java 想调用 Kotlin 伴生对象中的方法或属性。可以在伴生对象中的方法
 * 或属性上分别标注@JvmStatic、@JvmField，这样就可以像 Kotlin 一样来调用它们了。
 */
class Student {
    companion object {
        private var username = "Tony"
        private var marks = "A"

        fun printMarks() = println("The ${this.username}'s mark is ${this.marks}")

        fun changeMarks(marks: String) {
            this.marks = marks
        }
    }
}

/**
 * 使用伴生对象来创建懒汉模式的单例。
 */
class Singleton2 private constructor() {
    companion object {
        val instance: Singleton2 by lazy { Singleton2() }
    }

    fun printlnHelloWorld() = println("hello world")
}

abstract class Message {
    abstract var message: String
}

data class PingMsg(override var message: String="ping") : Message()
data class PongMsg(override var message: String="pong") : Message()
data class AskMsg(override var message: String="ask",
                      val body:Map<String,String>) : Message()


