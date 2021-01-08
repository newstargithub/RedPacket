package com.halo.redpacket.ui.fragment.location.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import com.halo.redpacket.ui.fragment.location.data.DBConfig.Companion.COLUMN_C_CODE
import com.halo.redpacket.ui.fragment.location.data.DBConfig.Companion.COLUMN_C_NAME
import com.halo.redpacket.ui.fragment.location.data.DBConfig.Companion.COLUMN_C_PINYIN
import com.halo.redpacket.ui.fragment.location.data.DBConfig.Companion.COLUMN_C_PROVINCE
import com.halo.redpacket.ui.fragment.location.data.DBConfig.Companion.DB_NAME
import com.halo.redpacket.ui.fragment.location.data.DBConfig.Companion.TABLE_NAME
import com.halo.redpacket.ui.fragment.location.model.City
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * 城市中心信息存储
 *
 * @author xuexiang
 * @since 2018/12/29 上午11:53
 */
class DBCityCenter(context: Context): ICityCenter {

    private var mDataBasePath: String
    private var mContext: Context

    init {
        mContext = context.applicationContext
        mDataBasePath = File.separator + "data" + Environment.getDataDirectory().absoluteFile + File.separator + mContext.getPackageName() + File.separator + "databases" + File.separator
        copyDBFile()
    }

    private fun copyDBFile() {
        val dir = File(mDataBasePath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        //创建新版本数据库
        val dbFile = File(mDataBasePath, DBConfig.DB_NAME)
        if (!dbFile.exists()) {
            try {
                val input = mContext.assets.open(DBConfig.DB_NAME)
                val os = FileOutputStream(dbFile)
                val buffer = ByteArray(DBCityCenter.BUFFER_SIZE)
                var read: Int = -1
                input.use { inStream->
                    os.use {
                        while (inStream.read(buffer, 0, buffer.size).also({ read = it }) != -1) {
                            it.write(buffer, 0, read)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        val BUFFER_SIZE = 1024
    }


    override fun getAllCities(): ArrayList<City> {
        val db = SQLiteDatabase.openOrCreateDatabase(mDataBasePath + DB_NAME, null)
        val cursor = db.rawQuery("select * from $TABLE_NAME", null)
        val result = ArrayList<City>()
        var city: City
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME))
            val province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE))
            val pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE))
            city = City(name, province, pinyin, code)
            result.add(city)
        }
        cursor.close()
        db.close()
        Collections.sort(result, CityComparator())
        return result
    }

    override fun searchCity(keyword: String): ArrayList<City> {
        val db = SQLiteDatabase.openOrCreateDatabase(mDataBasePath + DB_NAME, null)
        val sql = "select * from $TABLE_NAME where $COLUMN_C_NAME like ? or $COLUMN_C_PINYIN like ?"
        val cursor = db.rawQuery(sql, arrayOf("%$keyword%", "$keyword%"))
        val result = ArrayList<City>()
        var city: City
        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_C_NAME))
            val province = cursor.getString(cursor.getColumnIndex(COLUMN_C_PROVINCE))
            val pinyin = cursor.getString(cursor.getColumnIndex(COLUMN_C_PINYIN))
            val code = cursor.getString(cursor.getColumnIndex(COLUMN_C_CODE))
            city = City(name, province, pinyin, code)
            result.add(city)
        }
        cursor.close()
        db.close()
        Collections.sort(result, CityComparator())
        return result
    }

    /**
     * sort by a-z
     */
    class CityComparator : java.util.Comparator<City> {
        override fun compare(o1: City, o2: City): Int {
            val a = o1.pinyin.substring(0, 1)
            val b = o2.pinyin.substring(0, 1)
            return a.compareTo(b)
        }
    }
}