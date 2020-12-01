package com.halo.redpacket.kt

import android.content.Context
import android.view.View
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Pattern

class LambdaDemo {



}

fun sum(a: Int, b: Int, term: (Int) -> Int): Int {
    var sum = 0
    for (i : Int in a..b) {
        sum += term(i)
    }
    return sum
}

/**
 * 函数可以作为其他函数的返回值
 *
 */
fun sum(type: String): (Int, Int) -> Int {
    val identity = {x: Int -> x}
    val square = {x: Int -> x * x}
    val cube = {x: Int -> x * x * x}

    return when(type) {
        "identity" -> { a: Int, b: Int -> sum(a, b, identity) }
        "square" -> { a, b -> sum(a, b, square) }
        "cube" -> { a, b -> sum(a, b, cube) }
        else -> { a, b -> sum(a, b, identity) }
    }
}

fun setOnclickListener(view: View) {
    view.setOnClickListener(object : View.OnClickListener{
        override fun onClick(v: View?) {
        }
    })
    //简化为
    view.setOnClickListener {

    }
    //使用 Lambda 表达式：
    view.setOnClickListener({ v ->
        //...
    })
    //如果参数为函数类型并且是最后一个参数，那么可以将参数移到函数的括号外面：
    //如果参数只有一个 Lambda 表达式，那么函数的小括号可以省略：
    view.setOnClickListener {v ->
        //...
    }
    //最后，在点击事件里如果会使用到 view，那么" v -> "可以省略，使用默认参数it进行替代
    view.setOnClickListener {
        it.visibility = View.GONE
    }
}

fun nonInlined(block: () -> Unit) {// 不用内联的函数
    block()
}

inline fun inlined(block: () -> Unit) { // 使用内联的函数
    block()
}


fun main(args: Array<String>) {
    //调用新增的 sum 函数，它返回的是 (Int, Int) -> Int 。
    var identityFunction = sum("identity")
    println(identityFunction(1,10))
    println(identityFunction.invoke(1,10))

    //nonInlined 需要创建 Function0 对象
    nonInlined {
        println("do something with nonInlined")
    }
    inlined {
        println("do something with inlined")
    }

    val sourceString = "write something to test.txt"
    val sourceByte = sourceString.toByteArray()
    val file = File("test.txt")
    if (!file.exists()) {
        file.createNewFile()
    }
    FileOutputStream(file).use {// 使用了扩展函数 use 之后，就无需再主动关闭FileOutputStream
        it.write(sourceByte)
    }

    println("fengzhizi715@126.com".checkEmail())
}

/**
 * 对 Closeable 类进行扩展，让它支持 Java 的try-with-resources特性。
 * 这里用到了扩展函数（能够在不改变已有类的情况下，为某个类添加新的函数）
 *
 */
inline fun <T: Closeable?, R> T.use(block: (T) -> R): R {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            this?.close()
        } catch (closeException: Exception) {
        }
        throw e
    } finally {
        if (!closed) {
            this?.close()
        }
    }
}

/**
 * 扩展函数是形如类名.方法名()这样的形式。Kotlin 允许开发者在不改变已有类的情况下，为某个类添加新的函数，这个特性叫做扩展函数。
 * 对 Java 的 String 类增加一个 checkEmail() 函数，它的用途是判断字符串是否为电子邮件的格式。
 *
 * 扩展函数等价于下面的工具方法 checkEmail。
 * public static boolean checkEmail(String email) {
        String emailPattern = "[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
        return Pattern.matches(emailPattern, email);
    }

    当扩展函数跟原先的函数重名，并且参数都相同时，扩展函数就会失效，调用的是原先类的函数。
    扩展函数不具备多态性。无论传递的是父类 base 还是子类 child，最后执行的都是父类的扩展函数。
 */
fun String.checkEmail():Boolean {
    val emailPattern = "[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+"
    return Pattern.matches(emailPattern, this)
}

/**
 * 获取当前app的版本号
 */
fun Context.getVersion() :String {
    val appContext = applicationContext
    val pm = appContext.packageManager
    try {
        val info = pm.getPackageInfo(appContext.packageName, 0)
        if (info != null) {
            return info.versionName
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}