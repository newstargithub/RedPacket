package com.halo.redpacket.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: zx
 * Date: 2020/10/29
 * Description:
 */
public class SkinFactory implements LayoutInflater.Factory2 {
    private static final String TAG = SkinFactory.class.getSimpleName();
    private static final String[] PREFIX_LIST = {
      "android.widget.",
      "android.view.",
      "android.webkit."
    };
    private List<SkinView> list = new ArrayList<>();

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        Log.i(TAG, "SkinFactory:" + name);
        View view = null;
        if (name.contains(".")) {
            //带包名，为自定义View
            view = onCreateView(name, context, attrs);
        } else {
            //不带包名，为系统View
            for (String s : PREFIX_LIST) {
                String viewName = s + name;
                view = onCreateView(viewName, context, attrs);
                if (view != null) {
                    break;
                }
            }
        }
        if (view != null) {
            parseView(view, attrs);
        }
        return view;
    }

    /**
     * 判断是否是需要换肤的，是就保存起来
     * @param view
     * @param attrs
     */
    private void parseView(View view, AttributeSet attrs) {
        List<SkinItem> itemList = new ArrayList<>();
        SkinItem item;
        for(int i = 0; i < attrs.getAttributeCount(); i++) {
            String attributeName = attrs.getAttributeName(i);
            if (attributeName.contains("backgroud") ||
                    attributeName.contains("textColor") ||
                    attributeName.contains("src")) {
                //获取到资源的ID
                String attributeValue = attrs.getAttributeValue(i);
                int resId = Integer.parseInt(attributeValue);
                //获取到属性值得名字和类型
                String resourceEntryName = view.getResources().getResourceEntryName(resId);
                String resourceTypeName = view.getResources().getResourceTypeName(resId);
                item = new SkinItem();
                item.setResId(resId);
                item.setEntryName(resourceEntryName);
                item.setTypeName(resourceTypeName);
                item.setName(attributeName);
                itemList.add(item);
            }
        }
        if (itemList.size() > 0) {
            list.add(new SkinView(itemList, view));
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        try {
            Class<? extends View> clazz = (Class<? extends View>) context.getClassLoader().loadClass("name");
            Constructor<? extends View> constructor = clazz.getConstructor(Context.class, AttributeSet.class);
            view = constructor.newInstance(context, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void apply() {
        for (SkinView skinView : list) {
            skinView.apply();
        }
    }
}
