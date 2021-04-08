package com.iwhalecloud.tobacco.fragment.dialog

import android.widget.RadioButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.halo.redpacket.R
import com.halo.redpacket.databinding.DialogSignReasonBinding
import com.halo.redpacket.extend.clickWithTrigger
import com.halo.redpacket.extend.toast
import com.halo.redpacket.widget.dialog.BaseDialogFragment
import com.iwhalecloud.tobacco.R
import com.iwhalecloud.tobacco.databinding.DialogSignReasonBinding
import com.iwhalecloud.tobacco.databinding.DialogStaffResetPwdBinding
import com.iwhalecloud.tobacco.model.StaffEditModel
import com.iwhalecloud.tobacco.model.repository.InjectorUtil
import com.iwhalecloud.tobacco.utils.AppUtil
import com.iwhalecloud.tobacco.utils.ext.clickWithTrigger
import com.iwhalecloud.tobacco.view.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_sign_reason.*

/**
 * @author Zhouxin
 * @date :2021/4/7
 * @description: 代签理由弹窗
 */
class SignReasonDialog : BaseDialogFragment<SignModel, DialogSignReasonBinding>() {

    private var mReason: CharSequence? = null

    override fun initViewModel(): SignModel {
        return ViewModelProviders.of(this).get(SignModel::class.java)
    }

    override fun layoutId(): Int {
        return R.layout.dialog_sign_reason
    }

    override fun initView() {
        rg_sign_reason.setOnCheckedChangeListener { group, checkedId ->
            mReason = group.findViewById<RadioButton>(checkedId).text
        }
        tv_sign_cancel.clickWithTrigger {
            dismiss()
        }
        tv_sign_confirm.clickWithTrigger {
            viewModel?.sign(mReason)?.observe(this, Observer {
                if (it) {
                    onSignSuccess()
                } else {
                    onSignFail()
                }
            })
        }
    }

    override fun initData() {

    }

    private fun onSignSuccess() {
        toast("代签成功")
        dismiss()
    }

    private fun onSignFail() {
        toast("代签失败")
    }
}