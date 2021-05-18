package ws.gameServer.features.standalone.extp.talent.utils;

import ws.protos.EnumsProtos.ResourceTypeEnum;
import ws.relationship.base.IdAndCount;
import ws.relationship.base.IdMaptoCount;
import ws.relationship.exception.CmMessageIllegalArgumentException;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Talent_Row;

public class TalentCtrlUtils {
    public static Table_Talent_Row getTalentRow(int talentLevelId) {
        if (!RootTc.get(Table_Talent_Row.class).has(talentLevelId)) {
            String msg = String.format("Table_Talent_Row talentId=%s 不存在", talentLevelId);
            throw new CmMessageIllegalArgumentException(msg);
        }
        return RootTc.get(Table_Talent_Row.class).get(talentLevelId);
    }


    /**
     * 获取技能ID
     *
     * @param talentLevelId
     * @return
     */
    public static int getTalentId(int talentLevelId) {
        return getTalentRow(talentLevelId).getTalentId();
    }

    /**
     * 升级所需玩家等级
     *
     * @return
     */
    public static int needLevel(int talentLevelId) {
        if (talentLevelId == 400301) {
            System.out.println();
        }
        return getTalentRow(talentLevelId).getNeedCorpsLv();

    }

    /**
     * 需要前置天赋
     *
     * @param talentLevelId
     * @return
     */
    public static int needBeforeTalent(int talentLevelId) {
        return getTalentRow(talentLevelId).getBeforeTalent();
    }

    /**
     * 获取消耗天赋点
     *
     * @param talentLevelId
     * @return
     */
    private static int _needTalentPoint(int talentLevelId) {
        return getTalentRow(talentLevelId).getTalentPointUse();
    }

    /**
     * 获取消耗金币
     *
     * @param talentLevelId
     * @return
     */
    private static int _needMoney(int talentLevelId) {
        return getTalentRow(talentLevelId).getGoldUse();
    }


    /**
     * 获取升级天赋消耗资源
     *
     * @param talentLevelId
     * @return
     */
    public static IdMaptoCount getNeedResource(int talentLevelId) {
        int needPoint = _needTalentPoint(talentLevelId);
        int needMoney = _needMoney(talentLevelId);
        IdMaptoCount idMaptoCount = new IdMaptoCount();
        idMaptoCount.add(new IdAndCount(ResourceTypeEnum.RES_TALENT.getNumber(), needPoint));
        idMaptoCount.add(new IdAndCount(ResourceTypeEnum.RES_MONEY.getNumber(), needMoney));
        return idMaptoCount;
    }
}
