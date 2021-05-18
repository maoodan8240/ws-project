function getKey(keyId, keyObj) {
    var value = keyObj[keyId];
    if (value == undefined || value == null) {
        return keyId;
    }
    return value;

}


var PlayerInfoKey = {
    "_id": "玩家真实Id",
    "base.simpleId": "玩家简单Id",
    "base.name": "角色名",
    "account.platformType": "平台",
    "account.outerRealmId": "服Id",
    "base.level": "等级",
    "payment.vipLevel": "VIP等级",
    "payment.vipTotalExp": "VIP总经验",
    "account.createAt": "角色创建时间",

    "account.platformUid": "Uid",
    "base.sign": "签名",
    "base.sex": "性别",
    "base.iconId": "头像"
}


var ResourcePointInfoKey = {
    "resourceTypeMapToValue.\"RES_MONEY\"": "金币",
    "resourceTypeMapToValue.\"RES_VIPMONEY\"": "钻石",
    "resourceTypeMapToValue.\"RES_ENERGY\"": "体力",
    "resourceTypeMapToValue.\"RES_ROLE_EXP\"": "角色经验",
    "resourceTypeMapToValue.\"RES_VIP_EXP\"": "VIP经验",
    "resourceTypeMapToValue.\"RES_TALENT\"": "天赋",
    "resourceTypeMapToValue.\"RES_TEST_MONEY\"": "试炼币",
    "resourceTypeMapToValue.\"RES_ARENA_MONEY\"": "竞技场币",
    "resourceTypeMapToValue.\"RES_GUILD_MONEY\"": "社团币",
    "resourceTypeMapToValue.\"RES_AWAKE_FRAGMENT\"": "装备觉醒碎片",
}