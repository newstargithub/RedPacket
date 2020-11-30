package com.halo.redpacket.util

data class Person(var name:String, var password: String)

/**
 * 局部函数：是指在一个函数中定义另外一个函数。有点类似于内部类，局部函数可以访问外部函数的局部变量甚至闭包。
 */
fun validate(person: Person): Boolean {
    /**
     * 验证单个字符串输入的方法
     */
    fun validateInput(input: String?) {
        if (input == null || input.isEmpty()) {
            throw IllegalArgumentException("must not be empty")
        }
    }

    validateInput(person.name)
    validateInput(person.password)
    return true
}

fun printPerson(person: Person) {
    val name = person.name
    val password = person.password
    fun print() {
        println("name:$name password:$password")
    }
    print()
}

fun main(args: Array<String>) {
    val user1 = Person("tony","123456")
    println(validate(user1))
    printPerson(user1)

    val user2 = Person("tom","")
    println(validate(user2))


    val sum = {
        x: Int, y: Int -> x + y
    }
    //等价于
    val sum1:(Int, Int)-> Int = {x, y ->
        x + y
    }
}

/**
 * 尾递归，对自然数求和
 */
tailrec fun sumWithTailrec(n: Int, result: Int): Int  = if (n <= 0) result else sumWithTailrec(n-1, result + n)

/**
 * n!
 */
tailrec fun factorialWithTailrec(n: Int, result: Int): Int = if (n == 1) result else factorialWithTailrec(n-1, result * n)

class KtDemo {


}