package com.halo.redpacket

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.halo.redpacket.activity.PieImageActivity
import com.halo.redpacket.inject.*
import com.halo.redpacket.mvvm.LifeCycleListener
import java.util.*
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {
    private val arrays = arrayOf<Class<*>>(
            PieImageActivity::class.java,
            MainActivity::class.java,
            PmActivity::class.java
    )

    @Inject
    lateinit var user: UserInject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val listView = findViewById<View>(R.id.listView) as ListView
        val list = Arrays.asList(*arrays)
        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        listView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@HomeActivity, arrays[position])
            startActivity(intent)
        }

        val userModule3 = UserModule3()
        userModule3.param("userModule")
        DaggerUserComponent3.builder().userModule3(userModule3).build().inject(this)
        Toast.makeText(this, user.testInjectWithParam(), Toast.LENGTH_LONG).show()

        //绑定 LifeCycleListener 对象
        lifecycle.addObserver(LifeCycleListener())
    }
}