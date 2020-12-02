package com.halo.redpacket.model.bean

data class PM25Model(var position_name: String? = null, val quality: String?, val pm2_5: String?, val pm2_5_24h: String?) {
}

data class SO2Model(var position_name: String? = null, val so2: String?, val so2_24h: String?) {

}

data class ZipObject(var pm2_5_quality: String? = null, val pm2_5: String?, val pm2_5_24h: String?, val so2: String?, val so2_24h: String?)