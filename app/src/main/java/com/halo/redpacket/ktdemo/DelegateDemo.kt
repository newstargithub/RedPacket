package com.halo.redpacket.ktdemo

import io.reactivex.Observable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "${property.name}:$thisRef"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("value=$value")
    }

}

class UserD {
    var name by Delegate()
    var password by Delegate()
}


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
 *
 * 总结一下lateinit和by lazy的区别：
    lateinit 只能用于修饰变量 var，不能用于可空的属性和 Java 的基本类型。
    lateinit 可以在任何位置初始化并且可以初始化多次。
    lazy 只能用于修饰常量 val，并且 lazy 是线程安全的。
    lazy 在第一次被调用时就被初始化，以后调用该属性会返回之前的结果。
 */
val str: String by lazy {
    println("do somthing")
    "tony"
}

fun main() {
    testDelegateDemo1()
    testDelegateDemo2()

    println(str)
    println("-----------")
    println(str)

    testObservableFlatmap()
}

private fun testObservableFlatmap() {
    val subscribe = Observable.create<String> { e ->
        e.onNext("1")
        e.onNext("2")
        e.onNext("3")
        e.onNext("4")
        e.onComplete()
    }.map { s ->
        return@map Integer.parseInt(s)
    }.flatMap { s ->
        // 实际上我们可以根据自己的需求
        //创建一个逻辑十分复杂的Observable
        return@flatMap Observable.just(s)
                .filter { s ->
                    return@filter s != 3
                }.map { t ->
                    // do something
                    return@map "$t -map"
                }
    }.subscribe {
        println(it)
    }
}

fun testDelegateDemo1() {
    val u = UserD()

    println(u.name)
    u.name = "Tony"

    println(u.password)
    u.password = "123456"
}

fun testDelegateDemo2() {
    val tony = UserD2(1)
    println("tony.password="+tony.password)
    println("---------------------------")
    tony.password = "P@ssword"
    println("---------------------------")
    println("tony.password="+tony.password)
}

class DatabaseDelegate<in T, V>(private val field: String, private val id: Int) : ReadWriteProperty<T, V> {

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        return queryForValue(id, field) as V
    }

    private fun queryForValue(id: Int, field: String): Any {
        val value = data.firstOrNull {
            it["id"] == id
        }?.getValue(field) ?: NoRecordFoundException(id)
        println("loaded value $value for field \"$field\" of record $id")
        return value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        update(field, id, value)
    }

    private fun update(field: String, id: Int, value: V) {
        println("updating field \"$field\" of record $id to value $value")

        data.firstOrNull { it["id"] == id }
                ?.put(field, value)
                ?: throw NoRecordFoundException(id)
    }

    class NoRecordFoundException(id: Int): Exception("No record found for id $id")

    val data = arrayOf<MutableMap<String, Any?>>(
        mutableMapOf(
                "id" to 1,
                "name" to "Tony",
                "password" to "123456"
        ),
        mutableMapOf(
                "id" to 2,
                "name" to "Monica",
                "password" to "123456"
        )
    )

}

class UserD2(val id: Int) {
    var name: String by DatabaseDelegate("name", id) // 使用委托属性
    var password: String by DatabaseDelegate("password", id) // 使用委托属性
}
