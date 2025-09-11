package com.akai.aicreator.constant;

public interface PointsConstant {

    /**
     * 积分类型
     */
    public static final String POINTS_TYPE_DAILY_LOGIN = "DAILY_LOGIN";
    public static final String POINTS_TYPE_INVITE_REGISTER = "INVITE_FRIEND";
    public static final String POINTS_TYPE_AI_CONSUME = "AI_CONSUME";
    public static final String POINTS_TYPE_RECHARGE = "RECHARGE";
    public static final String POINTS_TYPE_DEFAULT_REGISTER = "DEFAULT_REGISTER";

    /**
     * 积分规则
     */
    // 1积分 = 1000个Token
    public static final int POINTS_TO_TOKEN_RATIO = 1000;

    // 每日登录奖励积分
    public static final int DAILY_LOGIN_POINTS = 10;

    // 邀请好友奖励积分
    public static final int INVITE_FRIEND_POINTS = 25;
    //好友邀请注册账号
    public static final int InVITE_REGISTER_POINTS = 75;

    //注册账号默认积分
    public static final int REGISTER_DEFAULT_POINTS = 50;

    // 充值比例：1元 = 10积分
    public static final int RECHARGE_POINTS_PER_YUAN = 10;



    /**
     * 错误消息
     */
    public static final String ERROR_USER_NOT_EXIST = "用户不存在";
    public static final String ERROR_ALREADY_INITIALIZED = "已经初始化过了!";
    public static final String ERROR_INVALID_INVITE_CODE = "邀请码无效";
    public static final String ERROR_CREATE_USER_POINTS_FAILED = "创建用户积分信息失败!";
    public static final String ERROR_CREATE_POINTS_RECORD_FAILED = "创建用户积分记录失败";
    public static final String ERROR_CREATE_INVITE_RELATION_FAILED = "创建邀请关系失败!";
    public static final String ERROR_ADD_INVITER_POINTS_FAILED = "给邀请人添加积分失败!";
    public static final String ERROR_DAILY_REWARD_ALREADY_CLAIMED = "今日已领取登录奖励";
    public static final String ERROR_RECHARGE_AMOUNT_INVALID = "充值金额必须大于0";
}