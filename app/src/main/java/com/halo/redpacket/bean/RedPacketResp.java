package com.halo.redpacket.bean;

import java.io.Serializable;

/**
 * Description:
 */

public class RedPacketResp implements Serializable {

    private boolean withAnimation;
    private double redPocketAmount;
    private String account;

    public void setWithAnimation(boolean withAnimation) {
        this.withAnimation = withAnimation;
    }

    public boolean isWithAnimation() {
        return withAnimation;
    }

    public void setRedPocketAmount(double redPocketAmount) {
        this.redPocketAmount = redPocketAmount;
    }

    public double getRedPocketAmount() {
        return redPocketAmount;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }
}
