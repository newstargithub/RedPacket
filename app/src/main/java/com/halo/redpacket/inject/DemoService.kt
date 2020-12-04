package com.halo.redpacket.inject

import javax.inject.Inject

class DemoService: IService {
    override fun foo(): String = "this is from DemoService"
}