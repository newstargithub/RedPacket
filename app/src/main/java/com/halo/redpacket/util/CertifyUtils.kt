package com.halo.redpacket.util

import android.content.Context
import com.halo.redpacket.App
import okhttp3.OkHttpClient
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class CertifyUtils {
    companion object {
        private val certifyAssetName: String = "file.cert"

        /**
         * 服务器证书 vs 本地证书
         * 1.发布方的标识名 是否一致
         * 2.主体的标识名   是否一致
         */
        fun getSSLClientIgnoreExpire(client: OkHttpClient, context: Context = App.instance!!.applicationContext, assetsSSLFileName: String = certifyAssetName): OkHttpClient {
            getStream(context, assetsSSLFileName).use {
                //Certificate
                val certificateFactory = CertificateFactory.getInstance("X.509")
                var certificate: Certificate? = null
                val pubSub: String
                val pubIssuer: String
                certificate = certificateFactory.generateCertificate(it)
                val pubSubjectDN = (certificate as X509Certificate).subjectX500Principal
                val pubIssuerDN = certificate.issuerX500Principal
                pubSub = pubSubjectDN.name
                pubIssuer = pubIssuerDN.name

                // Create an SSLContext that uses our TrustManager
                val trustManagers = arrayOf<X509TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                        try {
                            val tmf = TrustManagerFactory.getInstance("X509")
                            tmf.init(null as KeyStore?)
                            for (trustManager in tmf.trustManagers) {
                                (trustManager as X509TrustManager).checkClientTrusted(chain, authType)
                            }
                        } catch (e: Exception) {
                            throw CertificateException(e)
                        }
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                        //1、判断证书是否是本地信任列表里颁发的证书
                        try {
                            val tmf = TrustManagerFactory.getInstance("X509")
                            tmf.init(null as KeyStore?)
                            for (trustManager in tmf.trustManagers) {
                                (trustManager as X509TrustManager).checkServerTrusted(chain, authType)
                            }
                        } catch (e: Exception) {
                            throw CertificateException(e)
                        }

                        //2、判断服务器证书 发布方的标识名  和 本地证书 发布方的标识名 是否一致
                        //3、判断服务器证书 主体的标识名  和 本地证书 主体的标识名 是否一致
                        //getIssuerDN()  获取证书的 issuer（发布方的标识名）值。
                        //getSubjectDN()  获取证书的 subject（主体的标识名）值。
                        if (chain[0].subjectX500Principal.name != pubSub) {
                            throw CertificateException("server's SubjectDN is not equals to client's SubjectDN")
                        }
                        if (chain[0].issuerX500Principal.name != pubIssuer) {
                            throw CertificateException("server's IssuerDN is not equals to client's IssuerDN")
                        }
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }
                })

                //SSLContext  and SSLSocketFactory
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustManagers, java.security.SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                //okhttpclient
                val builder = client.newBuilder()
                builder.sslSocketFactory(sslSocketFactory, trustManagers[0])
                return builder.build()
            }
        }

        private fun getStream(context: Context, assetsFileName: String): InputStream {
            return context.assets.open(assetsFileName)
        }
    }
}