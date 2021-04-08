package com.halo.redpacket.mvvm.lifecycle;

import androidx.lifecycle.MutableLiveData;

import com.halo.redpacket.bean.DialogBean;


/**
 * 文件名:    DialogLiveData
 * 描述:     对话框数据
 */

public final class DialogLiveData<T> extends MutableLiveData<T> {

    private DialogBean bean = new DialogBean();

    public void setValue(boolean isShow) {
        try {
            bean.setShow(isShow);
            bean.setMsg("");
            setValue((T) bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setValue(boolean isShow, String msg) {
        try {
            bean.setShow(isShow);
            bean.setMsg(msg);
            setValue((T) bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}