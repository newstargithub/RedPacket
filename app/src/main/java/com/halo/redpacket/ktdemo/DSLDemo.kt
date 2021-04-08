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

    val user2 = user {
        name = "张三"
        password = "123456"
        address {
            province = "Jiangsu"
            city = "Suzhou"
            street = "Renming Road"
        }
    }
    println(user2)
}

class UserWrapper {
    private val address = Address()
    var name:String?=null
    var password: String?=null

    /**
     * Lambdas with Receiver（带接收者的 Lambda）
     */
    fun address(init: Address.() -> Unit): Address {
        address.init()
        return address
    }

    internal fun getAddress() = address
}

fun user(init: UserWrapper.() -> Unit): User {
    val wrapper = UserWrapper()
    wrapper.init()

    val user = User()
    user.username = wrapper.name
    user.password = wrapper.password
    user.address = wrapper.getAddress()
    return user
}