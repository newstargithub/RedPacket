package com.halo.redpacket.ktdemo

/**
 * 带接收者的函数类型
 * 带接收者的函数类型，例如 A.(B) -> C，其中 A 是接收者类型，B是参数类型，C是返回类型。
 */
class DSLDemo {

}

fun main() {
    val user = User().apply {
        username = "张三"
        password = "123456"
    }
    println(user)
}