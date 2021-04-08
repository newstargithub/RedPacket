package com.halo.redpacket.widget.dialog

import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProviders
import com.halo.redpacket.R
import com.halo.redpacket.bean.Agreement
import com.halo.redpacket.databinding.DialogAgreementBinding
import com.halo.redpacket.extend.*
import com.halo.redpacket.mvvm.vm.UserModel


/**
 * @author Zhouxin
 * @date :2021/2/9
 * @description:
 */
class AgreementDialog: BaseDialogFragment<UserModel, DialogAgreementBinding>() {

    private var mAgreement: Agreement? = null

    companion object {
        const val KEY_DATA = "data"
        fun newInstance(data: Agreement): AgreementDialog {
            val fragment = AgreementDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_DATA, data)
                }
            }
            return fragment
        }
    }

    override fun initViewModel(): UserModel {
        return ViewModelProviders.of(this).get(UserModel::class.java)
    }

    override fun layoutId(): Int {
        return R.layout.dialog_agreement
    }

    override fun initView() {
        isCancelable = false
    }

    override fun initData() {
        mAgreement = arguments?.getParcelable<Agreement>(KEY_DATA)
        dataBinding?.apply {
            tvTitle.text = mAgreement?.title
            tvAgreementContent.text = mAgreement?.content
            cbAgreementAgree.text = getString(R.string.f_agree_agreement, mAgreement?.title)
            btAgreementEnter.clickWithTrigger {
                signAgreement()
            }
            viewOverCb.setOnClickListener {
                toast(R.string.please_read_agreement)
            }
            cbAgreementAgree.setOnCheckedChangeListener { _, isChecked ->
                btAgreementEnter.isEnabled = isChecked
            }
            cbAgreementAgree.post {
                val canScrollDown = svAgreement.canScrollVertically(1)
                cbAgreementAgree.isEnabled = !canScrollDown
                cbAgreementAgree.isEnabled.then(viewOverCb.gone(), viewOverCb.visible())
            }
            svAgreement.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
                val canScrollDown = svAgreement.canScrollVertically(1)
                if (!canScrollDown) {
                    cbAgreementAgree.isEnabled = true
                    viewOverCb.gone()
                }
            }
        }
    }

    /**
     * 签署协议
     */
    private fun signAgreement() {
        enterSystem()
    }

    private fun enterSystem() {
        dismiss()
    }
}