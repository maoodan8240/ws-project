package ws.relationship.utils.attrs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.ColorDetailTypeEnum;
import ws.protos.EnumsProtos.EquipmentPositionEnum;
import ws.protos.EnumsProtos.FightTargetConditonEnum;
import ws.protos.EnumsProtos.FightTargetEnum;
import ws.protos.EnumsProtos.HeroAttrPosTypeEnum;
import ws.protos.EnumsProtos.HeroAttrTypeEnum;
import ws.protos.EnumsProtos.HeroPositionEnum;
import ws.protos.EnumsProtos.SexEnum;
import ws.protos.EnumsProtos.YokeTypeEnum;
import ws.relationship.base.HeroAttr;
import ws.relationship.base.HeroAttrs;
import ws.relationship.base.MagicNumbers;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_BadgeCheats_Row;
import ws.relationship.table.tableRows.Table_CardUpgradeQuality_Row;
import ws.relationship.table.tableRows.Table_CardWarSoul_Row;
import ws.relationship.table.tableRows.Table_CardYoke_Row;
import ws.relationship.table.tableRows.Table_Equipment_Row;
import ws.relationship.table.tableRows.Table_New_Card_Row;
import ws.relationship.table.tableRows.Table_Talent_Row;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.formations.FormationPos;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.talent.Talent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HeroAttrsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeroAttrsUtils.class);

    /**
     * 计算所有属性中的步长属性
     *
     * @param heroAttrs
     * @param lv
     */
    public static void calcuAndConvertAllStepAttr(HeroAttrs heroAttrs, int lv, boolean isPrecent) {
        HeroAttrs heroAttrsNew = new HeroAttrs();
        heroAttrs.getRaw().forEach((attrType, value) -> {
            HeroAttr stepAttr = calcuAndConvertStepAttr(attrType, value, lv, isPrecent);
            heroAttrsNew.add(new HeroAttr(stepAttr.getAttrType(), stepAttr.getAttrValue()));
        });
        heroAttrs.clear();
        heroAttrs.addAll(heroAttrsNew);
    }

    /**
     * 计算步长类属性对应的增量属性
     *
     * @param attrType
     * @param stepAttrValue
     * @param lv
     * @return
     */
    public static HeroAttr calcuAndConvertStepAttr(HeroAttrTypeEnum attrType, long stepAttrValue, int lv, boolean isPrecent) {
        long value = lv * stepAttrValue;
        switch (attrType) {

            case AttackStep:
                attrType = HeroAttrTypeEnum.Attack;
                value = isPrecent ? value / MagicNumbers.RANDOM_BASE_VALUE : value;
                break;
            case DefenseStep:
                attrType = HeroAttrTypeEnum.Defense;
                value = isPrecent ? value / MagicNumbers.RANDOM_BASE_VALUE : value;
                break;
            case HPStep:
                attrType = HeroAttrTypeEnum.HP;
                value = isPrecent ? value / MagicNumbers.RANDOM_BASE_VALUE : value;
                break;

            case AttackPlusStep:
                attrType = HeroAttrTypeEnum.AttackPlus;
                value = isPrecent ? value / MagicNumbers.RANDOM_BASE_VALUE : value;
                break;
            case DefensePlusStep:
                attrType = HeroAttrTypeEnum.DefensePlus;
                value = isPrecent ? value / MagicNumbers.RANDOM_BASE_VALUE : value;
                break;
            case HPPlusStep:
                attrType = HeroAttrTypeEnum.HPPlus;
                value = isPrecent ? value / MagicNumbers.RANDOM_BASE_VALUE : value;
                break;

            default:
                value = stepAttrValue;
        }
        return new HeroAttr(attrType, value);
    }


    /**
     * 移除步长类属性
     *
     * @param heroAttrs
     */
    public static void removeStepAttr(HeroAttrs heroAttrs) {
        heroAttrs.remove(HeroAttrTypeEnum.AttackStep);
        heroAttrs.remove(HeroAttrTypeEnum.DefenseStep);
        heroAttrs.remove(HeroAttrTypeEnum.HPStep);
        heroAttrs.remove(HeroAttrTypeEnum.AttackPlusStep);
        heroAttrs.remove(HeroAttrTypeEnum.DefensePlusStep);
        heroAttrs.remove(HeroAttrTypeEnum.HPPlusStep);
    }


    /**
     * 计算武将比例属性
     *
     * @param hero
     * @return
     */
    public static HeroAttrs calcuHeroRateAttr(Hero hero, HeroAttrs heroAttrs) {
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
        Table_CardUpgradeQuality_Row cardUpgradeQualityRow = Table_CardUpgradeQuality_Row.getCardUpgradeQualityRow(hero.getTpId(), ColorDetailTypeEnum.valueOf(hero.getQualityLv()));
        long newAttackValue = (heroAttrs.get(HeroAttrTypeEnum.Attack) * cardRow.getAttackRate() * cardUpgradeQualityRow.getAttackRate()) / (MagicNumbers.RANDOM_BASE_VALUE * MagicNumbers.RANDOM_BASE_VALUE) +/* */ heroAttrs.get(HeroAttrTypeEnum.Attack) * hero.getStarLv() * 2;
        long newDefenseValue = (heroAttrs.get(HeroAttrTypeEnum.Defense) * cardRow.getDefenseRate() * cardUpgradeQualityRow.getDefenseRate()) / (MagicNumbers.RANDOM_BASE_VALUE * MagicNumbers.RANDOM_BASE_VALUE) +/* */ heroAttrs.get(HeroAttrTypeEnum.Defense) * hero.getStarLv() * 2;
        long newHPValue = (heroAttrs.get(HeroAttrTypeEnum.HP) * cardRow.gethPRate() * cardUpgradeQualityRow.gethPRate()) / (MagicNumbers.RANDOM_BASE_VALUE * MagicNumbers.RANDOM_BASE_VALUE) + /* */heroAttrs.get(HeroAttrTypeEnum.HP) * hero.getStarLv() * 2;
        HeroAttrs heroAttrsNew = new HeroAttrs();
        heroAttrsNew.add(new HeroAttr(HeroAttrTypeEnum.Attack, newAttackValue));
        heroAttrsNew.add(new HeroAttr(HeroAttrTypeEnum.Defense, newDefenseValue));
        heroAttrsNew.add(new HeroAttr(HeroAttrTypeEnum.HP, newHPValue));
        return heroAttrsNew;
    }


    /**
     * 计算所有天赋增加的属性
     *
     * @param talent
     */
    public static Map<HeroAttrsRange, HeroAttrs> calcuTalentAttrs(Talent talent) {
        Map<HeroAttrsRange, HeroAttrs> rangeToHeroAttrs = new HashMap<>();
        talent.getTalentLevelIds().forEach(levelIds -> {
            Table_Talent_Row talentRow = RootTc.get(Table_Talent_Row.class, levelIds);
            for (int i = 0; i < talentRow.getTalentRange().size(); i++) {
                FightTargetEnum target = FightTargetEnum.valueOf(talentRow.getTalentRange().get(i));
                FightTargetConditonEnum conditon = FightTargetConditonEnum.valueOf(talentRow.getTalentRangeCondition().get(i));
                HeroAttrsRange range = new HeroAttrsRange(talentRow.getPlayType(), target, conditon);
                rangeToHeroAttrs.put(range, talentRow.getHeroAttr(i));
            }
        });
        return rangeToHeroAttrs;
    }


    /**
     * 计算武将装备增加的属性
     *
     * @param hero
     * @return
     */
    public static HeroAttrs calcuHeroEquipAtts(Hero hero) {
        HeroAttrs heroAttrs = new HeroAttrs();
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
        hero.getEquips().forEach((pos, equip) -> {
            if (isSimpleEquipPos(pos)) {
                Table_Equipment_Row equipmentRow = RootTc.get(Table_Equipment_Row.class, cardRow.getEquipmentTpId(pos));
                heroAttrs.addAll(equipmentRow.getEquipmentHeroAttrs(equip));
            } else if (isSpecialEquipPos(pos)) {
                Table_BadgeCheats_Row badgeCheatsRow = RootTc.get(Table_BadgeCheats_Row.class, cardRow.getEquipmentTpId(pos));
                heroAttrs.addAll(badgeCheatsRow.getBadgeCheatsHeroAttrs(equip));
            }
        });
        return heroAttrs;
    }

    /**
     * 计算所有获得卡牌的缘分属性
     *
     * @param hero
     * @param idToHero
     */
    public static HeroAttrs calcuAllYokeAttr_Card(Hero hero, Map<Integer, Hero> idToHero) {
        HeroAttrs heroAttrs = new HeroAttrs();
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
        for (int yokeId : cardRow.getCardYoke()) {
            Table_CardYoke_Row cardYokeRow = RootTc.get(Table_CardYoke_Row.class, yokeId);
            if (cardYokeRow.getYokeType() == YokeTypeEnum.YOKE_CARD) {
                heroAttrs.addAll(_calcuYokeAttr_Card(cardYokeRow, idToHero));
            }
        }
        return heroAttrs;
    }

    /**
     * 计算所有装备觉醒的缘分属性
     *
     * @param hero
     */
    public static HeroAttrs calcuAllYokeAttr_Equip_EF(Hero hero) {
        HeroAttrs heroAttrs = new HeroAttrs();
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
        for (int yokeId : cardRow.getCardYoke()) {
            Table_CardYoke_Row cardYokeRow = RootTc.get(Table_CardYoke_Row.class, yokeId);
            if (cardYokeRow.getYokeType() == YokeTypeEnum.YOKE_EQUIP_E_AWAKE) {
                heroAttrs.addAll(_calcuYokeAttr_Equip(cardYokeRow, hero, EquipmentPositionEnum.POS_E));
            } else if (cardYokeRow.getYokeType() == YokeTypeEnum.YOKE_EQUIP_F_AWAKE) {
                heroAttrs.addAll(_calcuYokeAttr_Equip(cardYokeRow, hero, EquipmentPositionEnum.POS_F));
            }
        }
        return heroAttrs;
    }

    /**
     * 卡牌卡片缘分需要的武将是否都存在,都存在则增加相应的属性
     *
     * @param cardYokeRow
     * @param idToHero
     * @return
     */
    private static HeroAttrs _calcuYokeAttr_Card(Table_CardYoke_Row cardYokeRow, Map<Integer, Hero> idToHero) {
        if (contailsAllHeroIpId(idToHero, cardYokeRow.getMainCondition())) {
            return cardYokeRow.getHeroAttrs();
        }
        return new HeroAttrs();
    }


    /**
     * 装备位E或F是否已经觉醒,觉醒则增加相应的属性
     *
     * @param cardYokeRow
     * @param hero
     * @param equipmentPosition
     * @return
     */
    private static HeroAttrs _calcuYokeAttr_Equip(Table_CardYoke_Row cardYokeRow, Hero hero, EquipmentPositionEnum equipmentPosition) {
        if (hero.getEquips().containsKey(equipmentPosition)) {
            if (hero.getEquips().get(equipmentPosition).getStarLv() > MagicNumbers.DefaultValue_Equipment_Star_Level) {
                return cardYokeRow.getHeroAttrs();
            }
        }
        return new HeroAttrs();
    }


    /**
     * 计算战魂增加的属性
     *
     * @param hero
     * @return
     */
    public static Map<HeroAttrsRange, HeroAttrs> calcuWarSoulAttr(Hero hero) {
        Map<HeroAttrsRange, HeroAttrs> rangeToHeroAttrs = new HashMap<>();
        hero.getSouls().forEach((pos, lvObj) -> {
            try {
                Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, hero.getTpId());
                int warSoulId = cardRow.getWarSoulId(pos);
                Table_CardWarSoul_Row warSoulRow = RootTc.get(Table_CardWarSoul_Row.class, warSoulId);
                FightTargetEnum target = FightTargetEnum.SELF;
                HeroAttrsRange range = new HeroAttrsRange(warSoulRow.getPlayType(), target, null);
                rangeToHeroAttrs.put(range, warSoulRow.getHeroAttr(lvObj.getLevel()));
            } catch (Exception e) {
                LOGGER.error("战魂属性结算:HeroTpId={}, pos={} .", hero.getTpId(), pos, e);
            }
        });
        return rangeToHeroAttrs;
    }


    /**
     * 所有的武将的TpId是否都已经存在
     *
     * @param idToHero      Heros target 中的所有武将
     * @param checkHeroTpId
     * @return
     */
    public static boolean contailsAllHeroIpId(Map<Integer, Hero> idToHero, List<Integer> checkHeroTpId) {
        if (checkHeroTpId.size() <= 0) {
            return false;
        }
        for (int tpId : checkHeroTpId) {
            boolean find = false;
            for (Entry<Integer, Hero> entry : idToHero.entrySet()) {
                if (entry.getValue().getTpId() == tpId) {
                    find = true;
                }
            }
            if (!find) {
                return false;
            }
        }
        return true;
    }


    /**
     * 合并相同的属性
     *
     * @param heroAttrsNew
     * @param heroAttrsOri
     * @param isFiex       是否是固定的属性
     */
    public static void mergeAttrs(HeroAttrs heroAttrsNew, HeroAttrs heroAttrsOri, boolean isFiex) {
        heroAttrsOri.getRaw().forEach((attrType, value) -> {
            if (isFiex) {
                if (HeroAttrsUtils.isFixedAttr(attrType)) {
                    heroAttrsNew.add(new HeroAttr(attrType, value));
                }
            } else {
                if (!HeroAttrsUtils.isFixedAttr(attrType)) {
                    heroAttrsNew.add(new HeroAttr(attrType, value));
                }
            }
        });
    }

    /**
     * 是否是固定值属性
     *
     * @param attrType
     * @return
     */
    public static boolean isFixedAttr(HeroAttrTypeEnum attrType) {
        switch (attrType) {
            case HP:
            case Defense:
            case Attack:
            case Anger:
            case StartAnger:
            case HitAnger:
            case HitedAnger:
            case KillAnger:
            case WaveAnger:
            case FriendDieAnger:
            case EnemyDieAnger:
            case RoundAnger:
            case Speed:
                return true;
            default:
                return false;
        }
    }


    /**
     * 其他属性
     * 生命 怒气 初始怒气 攻击怒气 被击怒气
     * 击杀怒气 轮换怒气 友军阵亡怒气 敌军阵亡怒气
     * 每回合怒气 小技能释放概率
     */

    /**
     * <pre>
     * 攻击类型的属性
     * 攻击 暴击率  暴击强度 破击率 伤害率 吸血 控制率 必杀伤害
     * </pre>
     *
     * @param attrType
     * @return
     */
    public static boolean isAttackAttr(HeroAttrTypeEnum attrType) {
        switch (attrType) {
            case Attack:
            case Crit:
            case CritDamage:
            case BlockBreak:
            case Damage:
            case Suck:
            case Ctrl:
            case AngerSkillDamage:
                return true;
            default:
                return false;
        }
    }

    /**
     * <pre>
     * 防御类型的属性
     * 防御 抗暴率 格挡率 格挡强度 免伤率 反伤 治疗率 免控率 必杀抗性
     *
     * 被攻属性格斗家攻击免伤增加 被防属性格斗家攻击免伤增加 被技属性格斗家攻击免伤增加
     * 被攻属性格斗家攻击免伤减少 被防属性格斗家攻击免伤减少 被技属性格斗家攻击免伤减少
     *
     * </pre>
     *
     * @param attrType
     * @return
     */
    public static boolean isDefenseAttr(HeroAttrTypeEnum attrType) {
        switch (attrType) {
            case Defense:
            case CritOppose:
            case Block:
            case BlockDamage:
            case DamageOppose:
            case Thorns:
            case Cure:
            case FreeCtrl:
            case AngerSkillDamageOppose:

            case BeAtByAttackDamageOpposeAdd:
            case BeAtByDefenseDamageOpposeAdd:
            case BeAtBySkillDamageOpposeAdd:

            case BeAtByAttackDamageOpposeReduce:
            case BeAtByDefenseDamageOpposeReduce:
            case BeAtBySkillDamageOpposeReduce:
                return true;
            default:
                return false;
        }
    }


    /**
     * 武将是否满足加属性的条件，目前支持： 全体 前排（垂直方向） 后排（垂直方向）
     *
     * @param range
     * @param belongHero
     * @param formation
     * @return
     */
    public static boolean meetFightTarget(HeroAttrsRange range, Hero belongHero, Formation formation) {
        if (range.getFightTarget() == FightTargetEnum.ALL) {
            return true;
        } else if (range.getFightTarget() == FightTargetEnum.FRONT_ROW || range.getFightTarget() == FightTargetEnum.BACK_ROW) {
            HeroPositionEnum position = getHeroPosition(belongHero, formation);
            if (position == null) { // 不在阵容上
                return false;
            }
            FightTargetEnum fightTargetTmp = getFightTargetOfHeroPosition(position);
            if (fightTargetTmp == range.getFightTarget()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 武将是否满足加属性的条件，目前只支持 男性 女性 攻属性 防属性 技属性
     *
     * @param range
     * @param belongHero
     * @return
     */
    public static boolean meetFightCondition(HeroAttrsRange range, Hero belongHero) {
        Table_New_Card_Row cardRow = RootTc.get(Table_New_Card_Row.class, belongHero.getTpId());
        if ((range.getConditon() == FightTargetConditonEnum.MALE_F) && (cardRow.getCardSexy() == SexEnum.MALE)) {
            return true;
        } else if ((range.getConditon() == FightTargetConditonEnum.FEMALE_F) && (cardRow.getCardSexy() == SexEnum.FEMALE)) {
            return true;
        } else if ((range.getConditon() == FightTargetConditonEnum.ATTACK_ATTR) && (cardRow.getCardType() == HeroAttrPosTypeEnum.ATTACK_ATTR_POS)) {
            return true;
        } else if ((range.getConditon() == FightTargetConditonEnum.DEFEND_ATRR) && (cardRow.getCardType() == HeroAttrPosTypeEnum.DEFEND_ATRR_POS)) {
            return true;
        } else if ((range.getConditon() == FightTargetConditonEnum.SKILL_ATTR) && (cardRow.getCardType() == HeroAttrPosTypeEnum.SKILL_ATTR_POS)) {
            return true;
        }
        return false;
    }


    /**
     * 获取武将是前排还是后排
     *
     * @param heroPosition
     * @return
     */
    private static FightTargetEnum getFightTargetOfHeroPosition(HeroPositionEnum heroPosition) {
        if (heroPosition == null) {
            return null;
        }
        switch (heroPosition) {
            case HERO_POSITION_ONE:
            case HERO_POSITION_TWO:
            case HERO_POSITION_THREE:
                return FightTargetEnum.FRONT_ROW;
            case HERO_POSITION_FOUR:
            case HERO_POSITION_FIVE:
            case HERO_POSITION_SIX:
                return FightTargetEnum.BACK_ROW;
        }
        return null;
    }

    /**
     * 获取武将在阵容中的位置
     *
     * @param belongHero
     * @param formation
     * @return
     */
    private static HeroPositionEnum getHeroPosition(Hero belongHero, Formation formation) {
        for (Entry<HeroPositionEnum, FormationPos> entry : formation.getPosToFormationPos().entrySet()) {
            int tmpHeroId = entry.getValue().getHeroId();
            if (tmpHeroId <= 0) {
                continue;
            }
            if (tmpHeroId == belongHero.getId()) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 计算属性百分比加成
     *
     * @param fix
     * @param precent
     */
    public static void calcuHeroAttrPlus(HeroAttrs fix, HeroAttrs precent) {
        long attackNew = fix.get(HeroAttrTypeEnum.Attack) * (MagicNumbers.RANDOM_BASE_VALUE + precent.get(HeroAttrTypeEnum.AttackPlus)) / MagicNumbers.RANDOM_BASE_VALUE;
        long defenseNew = fix.get(HeroAttrTypeEnum.Defense) * (MagicNumbers.RANDOM_BASE_VALUE + precent.get(HeroAttrTypeEnum.DefensePlus)) / MagicNumbers.RANDOM_BASE_VALUE;
        long hpNew = fix.get(HeroAttrTypeEnum.HP) * (MagicNumbers.RANDOM_BASE_VALUE + precent.get(HeroAttrTypeEnum.HPPlus)) / MagicNumbers.RANDOM_BASE_VALUE;
        fix.replace(new HeroAttr(HeroAttrTypeEnum.Attack, attackNew));
        fix.replace(new HeroAttr(HeroAttrTypeEnum.Defense, defenseNew));
        fix.replace(new HeroAttr(HeroAttrTypeEnum.HP, hpNew));
        precent.remove(HeroAttrTypeEnum.AttackPlus);
        precent.remove(HeroAttrTypeEnum.DefensePlus);
        precent.remove(HeroAttrTypeEnum.HPPlus);
    }


    /**
     * 是否是普通装备位置，包括（A B C D）
     *
     * @param pos
     * @return
     */
    private static boolean isSimpleEquipPos(EquipmentPositionEnum pos) {
        if (pos == EquipmentPositionEnum.POS_A || pos == EquipmentPositionEnum.POS_B
                || pos == EquipmentPositionEnum.POS_C || pos == EquipmentPositionEnum.POS_D) {
            return true;
        }
        return false;
    }

    /**
     * 是否是特殊的装备位置，包括（E F）
     *
     * @param pos
     * @return
     */
    private static boolean isSpecialEquipPos(EquipmentPositionEnum pos) {
        if (pos == EquipmentPositionEnum.POS_E || pos == EquipmentPositionEnum.POS_F) {
            return true;
        }
        return false;
    }


    /**
     * 打印有值的属性
     *
     * @param heroAttrs
     * @return
     */
    public static String printHeroAttrs(HeroAttrs heroAttrs) {
        String pc = "";
        for (HeroAttrTypeEnum type : HeroAttrTypeEnum.values()) {
            Long value = heroAttrs.getRaw().get(type);
            if (value != null && value > 0) {
                pc += type.name() + " : " + value + "\n";
            }
        }
        return pc;
    }
}

