package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.interfaces.cell.ListCell;
import ws.common.table.table.utils.CellParser;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.YokeTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table_CardYoke_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * 获得的武将---影响的武将及对应的缘分Id
     */
    private static Map<Integer, Map<Integer, Integer>> effectHeroTpIdToYokeId = new HashMap<>();
    /**
     * string 缘分名称
     */
    private String yokeNameId;
    /**
     * string 缘分说明
     */
    private String yokeExplainId;
    /**
     * int 缘分类型
     */
    private Integer yokeType;
    /**
     * int 缘分影响
     */
    private Integer yokeEffectType;
    /**
     * arrayint 条件卡片
     */
    private ListCell<Integer> mainCondition;

    private Integer belongHero;

    private HeroAttrs heroAttrs = new HeroAttrs();

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"缘分ID"}
        yokeNameId = CellParser.parseSimpleCell("YokeNameId", map, String.class); //string
        yokeExplainId = CellParser.parseSimpleCell("YokeExplainId", map, String.class); //string
        yokeType = CellParser.parseSimpleCell("YokeType", map, Integer.class); //int
        yokeEffectType = CellParser.parseSimpleCell("YokeEffectType", map, Integer.class); //int
        mainCondition = CellParser.parseListCell("MainCondition", map, Integer.class); //string
        belongHero = CellParser.parseSimpleCell("BelongHero", map, Integer.class); //int
        addEffectHeroTpIds();
        parseHeroAttrs(map);
    }


    /**
     * heroTpId在belongHero的id缘分中
     */
    private void addEffectHeroTpIds() {
        if (this.mainCondition.getAll().size() <= 0) {
            return;
        }
        for (int heroTpId : this.mainCondition.getAll()) {
            addToeffectHeroTpIds(heroTpId, this.belongHero, this.id);
        }
    }


    /**
     * 获得某个武将影响的其他武将
     *
     * @param heroTpId
     * @param effectHeroTpId
     */
    private static void addToeffectHeroTpIds(int heroTpId, int effectHeroTpId, int yokeId) {
        if (!effectHeroTpIdToYokeId.containsKey(heroTpId)) {
            effectHeroTpIdToYokeId.put(heroTpId, new HashMap<>());
        }
        Map<Integer, Integer> effects = effectHeroTpIdToYokeId.get(heroTpId);
        effects.put(effectHeroTpId, yokeId);
    }


    /**
     * 解析属性字段
     *
     * @param map
     */
    private void parseHeroAttrs(Map<String, String> map) {
        heroAttrs.clear();
        for (HeroAttrTypeEnum attrType : HeroAttrTypeEnum.values()) {
            String attrName = attrType.toString();
            if (map.containsKey(attrName)) {
                heroAttrs.add(new HeroAttr(attrType, Integer.valueOf(map.get(attrName)).longValue()));
            }
        }
    }

    public HeroAttrs getHeroAttrs() {
        return new HeroAttrs(heroAttrs);
    }

    public static Map<Integer, Map<Integer, Integer>> getEffectHeroTpIdToYokeId() {
        return Collections.unmodifiableMap(effectHeroTpIdToYokeId);
    }

    public String getYokeNameId() {
        return yokeNameId;
    }

    public String getYokeExplainId() {
        return yokeExplainId;
    }

    public YokeTypeEnum getYokeType() {
        return YokeTypeEnum.valueOf(yokeType);
    }

    public Integer getYokeEffectType() {
        return yokeEffectType;
    }

    public List<Integer> getMainCondition() {
        return mainCondition.getAll();
    }

    public Integer getBelongHero() {
        return belongHero;
    }


}
