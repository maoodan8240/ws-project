package ws.gameServer.features.standalone.extp.dataCenter.enums;

import ws.relationship.exception.BusinessLogicMismatchConditionException;

import java.util.ArrayList;
import java.util.List;

/**
 * code属性一旦确定，不可更改，涉及到持久化
 */
public enum PrivateNotifyTypeEnum {
    // --------------------------- Player ----------------------------------------
    Player_LoginAllDays(100, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),              // 玩家累计登录的天数
    Player_LoginOneDay(101, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),                // 每日-玩家今天登录了
    Player_LevelUp(102, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),                   // 玩家升级

    // --------------------------- Heros ----------------------------------------
    // 201-206 code不能改变，对应于ColorDetailTypeEnum的各自枚举值
    Heros_WHITE_QualityNum(201, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=白色品质武将的数量
    Heros_GREEN_QualityNum(202, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=绿色品质武将的数量
    Heros_GREEN_1_QualityNum(203, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),         // >=绿色+1品质武将的数量
    Heros_GREEN_2_QualityNum(204, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),         // >=绿色+2品质武将的数量
    Heros_BLUE_QualityNum(205, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),            // >=蓝色品质武将的数量
    Heros_BLUE_1_QualityNum(206, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),          // >=蓝色+1品质武将的数量
    Heros_BLUE_2_QualityNum(207, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),          // >=蓝色+2品质武将的数量
    Heros_BLUE_3_QualityNum(208, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),          // >=蓝色+3品质武将的数量
    Heros_PURPLE_QualityNum(209, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),          // >=紫色品质武将的数量
    Heros_PURPLE_1_QualityNum(210, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=紫色+1品质武将的数量
    Heros_PURPLE_2_QualityNum(211, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=紫色+2品质武将的数量
    Heros_PURPLE_3_QualityNum(212, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=紫色+3品质武将的数量
    Heros_PURPLE_4_QualityNum(213, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=紫色+4品质武将的数量
    Heros_ORANGE_QualityNum(214, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),          // >=橙色品质武将的数量
    Heros_ORANGE_1_QualityNum(215, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=橙色+1品质武将的数量
    Heros_ORANGE_2_QualityNum(216, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=橙色+2品质武将的数量
    Heros_ORANGE_3_QualityNum(217, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=橙色+3品质武将的数量
    Heros_ORANGE_4_QualityNum(218, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=橙色+4品质武将的数量
    Heros_ORANGE_5_QualityNum(219, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),        // >=橙色+5品质武将的数量
    Heros_RED_QualityNum(220, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),             // >=红色品质武将的数量
    Heros_RED_1_QualityNum(221, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=红色+1品质武将的数量
    Heros_RED_2_QualityNum(222, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=红色+2品质武将的数量
    Heros_RED_3_QualityNum(223, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=红色+3品质武将的数量
    Heros_RED_4_QualityNum(224, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=红色+4品质武将的数量
    Heros_RED_5_QualityNum(225, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=红色+5品质武将的数量
    Heros_RED_6_QualityNum(226, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),           // >=红色+6品质武将的数量

    Heros_HasCollectHeroNum(230, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),          // 已经收集到的武将的数量

    Heros_Daliy_UpSkill_Times(250, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),         // 每日-升级技能的次数
    Heros_Daliy_UpLv_Times(251, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),            // 每日-升级武将的次数
    Heros_Daliy_UpQualiy_Times(252, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),        // 每日-升级武将品级的次数

    // --------------------------- NewPve ----------------------------------------
    NewPve_DaliyAttackNormalTimes(300, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.ADD),       // 每日-攻打普通关卡的次数
    NewPve_DaliyAttackEliteNum(301, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.ADD),          // 每日-攻打精英关卡的次数

    NewPve_HasComplet_Stage(310, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),          // 已经完成通过的关卡

    // --------------------------- Month ----------------------------------------
    MonthCard(400, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER, true),                   // 每日-月卡250/880

    // --------------------------- Enery ----------------------------------------
    Energy_TimeGet(500, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER, true),              // 每日-体力任务，定点送体力
    Energy_DaliyBuy(501, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),                   // 每日-购买体力


    // --------------------------- PickCards ----------------------------------------
    PickCards_DaliyTimes(600, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),              // 每日-抽卡次数(包括抽卡Id: 1 & 2 的次数)

    // --------------------------- Arena ----------------------------------------
    Arean_DaliyAttackTimes(700, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),            // 每日-攻击次数

    // --------------------------- UltimateTest ----------------------------------------
    UltimateTest_CompletFloorLevel(800, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.COVER),    // 每日-挑战成功某一层


    // --------------------------- ResourcePoint ----------------------------------------
    ResourcePoint_DaliyConsumes(900, NotifyScopeEnum.DAILY, NotifyAddValueTypeEnum.ADD),         // 每日-消耗的货币数量


    // --------------------------- Payment ----------------------------------------
    Payment_FirstPay(1000, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),                // 首充
    Payment_DaliyPay(1001, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.ADD),                  // 每日充值

    // --------------------------- Buy ----------------------------------------
    Buy_Fund_Complete(1100, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),                // 购买基金


    //
    Other_General(9999900, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),                // 只是特殊任务解析所用
    Other_General_Exchange(9999901, NotifyScopeEnum.COMMON, NotifyAddValueTypeEnum.COVER),       // 只是特殊任务解析所用 -- 兑换
    //
    ;

    private int code;
    private NotifyScopeEnum scope;
    private NotifyAddValueTypeEnum addValueType;
    private boolean special; // 是否是特殊的Notify,需要单独处理

    PrivateNotifyTypeEnum(int code, NotifyScopeEnum scope, NotifyAddValueTypeEnum addValueType) {
        this(code, scope, addValueType, false);
    }

    PrivateNotifyTypeEnum(int code, NotifyScopeEnum scope, NotifyAddValueTypeEnum addValueType, boolean special) {
        this.code = code;
        this.scope = scope;
        this.addValueType = addValueType;
        this.special = special;
    }

    public NotifyScopeEnum getScope() {
        return scope;
    }

    public NotifyAddValueTypeEnum getAddValueType() {
        return addValueType;
    }

    public int getCode() {
        return code;
    }

    public boolean isSpecial() {
        return special;
    }

    /**
     * 检查code码的唯一性
     */
    public static void check() {
        List<Integer> codes = new ArrayList<>();
        for (PrivateNotifyTypeEnum type : PrivateNotifyTypeEnum.values()) {
            if (codes.contains(type.code)) {
                String msg = String.format("重复的code=%s type=%s !", type.code, type);
                throw new BusinessLogicMismatchConditionException(msg);
            }
            codes.add(type.code);
        }
    }


    /**
     * code转为对应的枚举
     *
     * @param code
     * @return
     */
    public static PrivateNotifyTypeEnum parse(int code) {
        for (PrivateNotifyTypeEnum type : PrivateNotifyTypeEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }
        String msg = String.format("解析code=%s 失败!", code);
        throw new BusinessLogicMismatchConditionException(msg);
    }
}
