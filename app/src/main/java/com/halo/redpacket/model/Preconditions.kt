package com.halo.redpacket.model

class Preconditions {
    companion object {
        fun isNotBlank(reference: Any?): Boolean {
            if (reference == null) {
                throw NullPointerException()
            }
            return true
        }
    }
}