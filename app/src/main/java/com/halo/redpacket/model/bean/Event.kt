package com.halo.redpacket.model.bean

import java.io.Serializable

/**
 * 使用 Kotlin 的 data class 来定义这些数据类。
 */
data class Event(var id: String,
                 var type: String,
                 var actor: Actor,
                 var repo: Repo,
                 var payload: Payload,
                 var public: Boolean,
                 var created_at: String): Serializable {
}

data class Actor(var id: String): Serializable

data class Repo(var id: String): Serializable

data class Payload(var id: String): Serializable
