package com.halo.redpacket.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.halo.redpacket.R
import com.halo.redpacket.ui.fragment.dialog.DialogFragment

class ContainerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        if (savedInstanceState != null) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fl_container)
            if (fragment == null) {
                val dialogFragment = DialogFragment()
                showFragment(dialogFragment)
            }
        } else {
            val dialogFragment = DialogFragment()
            showFragment(dialogFragment)
        }
    }

    private fun showFragment(dialogFragment: DialogFragment) {
        supportFragmentManager.beginTransaction().add(R.id.fl_container, dialogFragment).commit()
    }

}
