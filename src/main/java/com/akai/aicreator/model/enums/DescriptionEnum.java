package com.akai.aicreator.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum DescriptionEnum {
    REGISTER_REWARD("注册奖励", "REGISTER_REWARD"),
    INVITE_REGISTER_REWARD("邀请注册奖励", "INVITE_REGISTER_REWARD"),
    INVITE_FRIEND_REWARD("邀请好友奖励", "INVITE_FRIEND_REWARD"),
    DAILY_LOGIN_REWARD("每日登录奖励", "DAILY_LOGIN_REWARD"),
    AI_CONSUME("AI调用消耗", "AI_CONSUME"),
    RECHARGE_REWARD("充值获得积分", "RECHARGE_REWARD");
    private final String text;
    private final String value;

    DescriptionEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    public static DescriptionEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (DescriptionEnum anEnum : DescriptionEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
