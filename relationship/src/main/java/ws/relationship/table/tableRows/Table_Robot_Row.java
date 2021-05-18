package ws.relationship.table.tableRows;

import ws.common.table.table.exception.CellParseFailedException;
import ws.common.table.table.implement.AbstractRow;
import ws.common.table.table.utils.CellParser;
import ws.newBattle.NewBattleHeroContainer;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.RootTc;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.heros.Heros;

import java.util.HashMap;
import java.util.Map;

public class Table_Robot_Row extends AbstractRow {
    private static final long serialVersionUID = 1L;
    /**
     * string 名称
     */
    private String robotName;
    /**
     * int 等级
     */
    private Integer robotLevel;
    /**
     * int 战斗力
     */
    private Integer battleValue;
    /**
     * int 头像
     */
    private Integer headPhotoId;
    /**
     * string 宣言
     */
    private String declaration;
    /**
     * int 前排怪1
     */
    private Integer before1;
    /**
     * int 前排怪2
     */
    private Integer before2;
    /**
     * int 前排怪3
     */
    private Integer before3;
    /**
     * int 后排1
     */
    private Integer after1;
    /**
     * int 后排2
     */
    private Integer after2;
    /**
     * int 后排3
     */
    private Integer after3;


    public String getRobotName() {
        return robotName;
    }

    public Integer getRobotLevel() {
        return robotLevel;
    }

    public Integer getBattleValue() {
        return battleValue;
    }

    public Integer getHeadPhotoId() {
        return headPhotoId;
    }

    public String getDeclaration() {
        return declaration;
    }

    public Integer getBefore1() {
        return before1;
    }

    public Integer getBefore2() {
        return before2;
    }

    public Integer getBefore3() {
        return before3;
    }

    public Integer getAfter1() {
        return after1;
    }

    public Integer getAfter2() {
        return after2;
    }

    public Integer getAfter3() {
        return after3;
    }

    @Override
    public void parseRow(Map<String, String> map) throws CellParseFailedException {
        // id column = {columnName:"Id", columnDesc:"主id"}
        robotName = CellParser.parseSimpleCell("RobotName", map, String.class); //string
        robotLevel = CellParser.parseSimpleCell("RobotLevel", map, Integer.class); //int
        battleValue = CellParser.parseSimpleCell("BattleValue", map, Integer.class); //int
        headPhotoId = CellParser.parseSimpleCell("HeadPhotoId", map, Integer.class); //int
        declaration = CellParser.parseSimpleCell("Declaration", map, String.class); //string
        before1 = CellParser.parseSimpleCell("Before1", map, Integer.class); //int
        before2 = CellParser.parseSimpleCell("Before2", map, Integer.class); //int
        before3 = CellParser.parseSimpleCell("Before3", map, Integer.class); //int
        after1 = CellParser.parseSimpleCell("After1", map, Integer.class); //int
        after2 = CellParser.parseSimpleCell("After2", map, Integer.class); //int
        after3 = CellParser.parseSimpleCell("After3", map, Integer.class); //int
    }


    public NewBattleHeroContainer getRotbotHeroContainer() {
        Formation formation = new Formation();
        Heros heros = new Heros();
        Map<Integer, HeroAttrs> heroAttrsMap = new HashMap<>();

        addMonsterHero(heros, formation, heroAttrsMap, HeroPositionEnum.HERO_POSITION_ONE, before1);
        addMonsterHero(heros, formation, heroAttrsMap, HeroPositionEnum.HERO_POSITION_TWO, before2);
        addMonsterHero(heros, formation, heroAttrsMap, HeroPositionEnum.HERO_POSITION_THREE, before3);

        addMonsterHero(heros, formation, heroAttrsMap, HeroPositionEnum.HERO_POSITION_FOUR, after1);
        addMonsterHero(heros, formation, heroAttrsMap, HeroPositionEnum.HERO_POSITION_FIVE, after2);
        addMonsterHero(heros, formation, heroAttrsMap, HeroPositionEnum.HERO_POSITION_SIX, after3);

        NewBattleHeroContainer container = new NewBattleHeroContainer(formation, heros, heroAttrsMap);
        return container;
    }

    private Hero addMonsterHero(Heros heros, Formation formation, Map<Integer, HeroAttrs> heroAttrsMap, HeroPositionEnum positionEnum, int monsterId) {
        Table_Monster_Row monster = RootTc.get(Table_Monster_Row.class, monsterId);
        Hero hero = monster.getHero();
        hero.setId(positionEnum.getNumber() * MagicNumbers.MAX_MONSTER_ID + hero.getTpId());
        heros.getIdToHero().put(hero.getId(), hero);
        formation.getPosToFormationPos().put(positionEnum, new FormationPos(positionEnum, hero.getId()));
        heroAttrsMap.put(hero.getId(), monster.getHeroAttrs());
        return hero;
    }
}
