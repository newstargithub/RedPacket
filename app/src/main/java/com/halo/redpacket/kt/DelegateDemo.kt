package com.halo.redpacket.kt

/**
 * Author: zx
 * Date: 2020/12/1
 * Description:
 * 在某个类中，如果某些成员变量没办法在一开始就初始化，
 * 并且又不想使用可空类型(也就是带?的类型)。那么，可以使用lateinit或者by lazy来修饰它。
 */
class DelegateDemo {

    /**
     * 被lateinit修饰的变量，并不是不用初始化，它需要在生命周期流程中进行获取或者初始化。
     */
    lateinit var value :String

}



/**
 * 而 lazy() 是一个函数，可以接受一个 Lambda 表达式作为参数，第一次调用时会执行 Lambda 表达式，以后调用该属性会返回之前的结果。
 */
val str: String by lazy {
    print("do somthing")
    "tony"
}

fun main() {
    println(str)
    println("-----------")
    println(str)
}