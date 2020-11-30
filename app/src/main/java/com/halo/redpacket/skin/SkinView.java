package com.halo.redpacket.skin;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Author: zx
 * Date: 2020/10/29
 * Description: 每个要换肤的控件和属性集合
 */
public class SkinView {
    List<SkinItem> list;//所有需要替换的属性
    View view;//需要换肤的控件

    public SkinView(List<SkinItem> list, View view) {
        this.list = list;
        this.view = view;
    }

    public void apply() {
        for (SkinItem skinItem : list) {
            int resId = skinItem.resId;
            if (TextUtils.equals(skinItem.getName(), "background")) {
                if (TextUtils.equals(skinItem.getTypeName(), "color")) {
                    view.setBackgroundColor(SkinManager.getInstance().getColor(resId));
                } else if (TextUtils.equals(skinItem.getTypeName(), "drawable")
                    || TextUtils.equals(skinItem.getTypeName(), "mipmap")) {
                    view.setBackground(SkinManager.getInstance().getDrawable(resId));
                }
            } else if (TextUtils.equals(skinItem.getName(), "textColor")) {
                if (view instanceof TextView) {
                    ((TextView)view).setTextColor(SkinManager.getInstance().getColor(resId));
                }
            } else if (TextUtils.equals(skinItem.getName(), "src")) {
                if (view instanceof ImageView) {
                    ((ImageView)view).setImageDrawable(SkinManager.getInstance().getDrawable(resId));
                }
            }
        }
    }

    public List<SkinItem> getList() {
        return list;
    }

    public void setList(List<SkinItem> list) {
        this.list = list;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
