package ws.newBattle.utils;

/**
 * Created by zhangweiwei on 16-12-2.
 */
public class NewBattleAllEnums {

    public enum BuffContainerTypeEnum {
        TMP, ALIVE, FOREVER,
    }

    public enum NewAttrContainerType {
        CUR, ATK_TMP
    }

    /**
     * 根据生命判断的buff类型
     */
    public enum HpJudgeType {
        /**
         * 斩杀
         */
        BEHEAD,
        /**
         * 致命
         */
        FATAL,
        /**
         * 不屈
         */
        UNYIELDING,
    }

    public enum AngerResurgenceType {
        TO50, TO100,
    }

}
