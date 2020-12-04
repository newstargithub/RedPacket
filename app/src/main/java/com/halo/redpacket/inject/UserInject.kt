package com.halo.redpacket.inject

import javax.inject.Inject


class UserInject @Inject constructor() {

    lateinit var param: String

    constructor(param: String): this() {
        this.param = param
    }

    fun testInject() = "this is the first Inject"

    fun testModule() = "this is the second Inject"

    fun testInjectWithParam() =  "this is the Inject with parameter: $param"
}