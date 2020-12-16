package com.halo.redpacket.model.bean

import java.io.Serializable

/**
 * 数据类(Data Class) 数据类使用data关键字来修饰类。
 * 特性
编译器自动从主构造函数中声明的所有属性可以导出以下方法：
equals()/hashCode()方法
toString()方法
componentN()方法
copy()方法

 * 是一个范型类
 * 还是一个数据类(data class)。 data class 类似于 Java Bean，它只包含一些数据字段，编译器能够自动生成属性的get和set方法。
 */
data class HttpResponse<T>(
        var code: Int = -1, //0: 成功 1: xxx错误或过期 2: 业务逻辑错误 500:系统内部错误 998表示Token无效
        var message: String? = null,
        var data: T? = null
) : Serializable {
    //额外定义了一个 isOkStatus 属性
    val isOkStatus: Boolean
        get() = code == 1
}

open class BaseResponse<T>(
        var code: Int = -1, //0: 成功 1: xxx错误或过期 2: 业务逻辑错误 500:系统内部错误 998表示Token无效
        var message: String? = null,
        var data: T? = null
) : Serializable {
    //额外定义了一个 isOkStatus 属性
    val isOkStatus: Boolean
        get() = code == 1
}