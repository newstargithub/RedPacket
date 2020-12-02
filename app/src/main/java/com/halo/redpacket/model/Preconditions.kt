package com.halo.redpacket.model

class Preconditions {
    companion object {
        fun isNotBlank(obj: Any?): Boolean = obj == null
    }
}