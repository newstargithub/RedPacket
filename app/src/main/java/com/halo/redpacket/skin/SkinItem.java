package com.halo.redpacket.skin;

/**
 * Author: zx
 * Date: 2020/10/29
 * Description: 每条属性的封装对象
 */
public class SkinItem {
    String name;// 属性的名字 textColor,background
    String typeName;//属性的类型 color drawable mipmap
    String entryName;//属性的值得名字 colorPrimary
    int resId;//属性值在R文件中的id

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getEntryName() {
        return entryName;
    }

    public void setEntryName(String entryName) {
        this.entryName = entryName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
