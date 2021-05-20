package ws.relationship.utils.attrs;

import ws.protos.EnumsProtos.SystemModuleTypeEnum;
import ws.relationship.base.HeroAttrs;
import ws.relationship.topLevelPojos.formations.Formation;
import ws.relationship.topLevelPojos.heros.Hero;
import ws.relationship.topLevelPojos.talent.Talent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 17-4-27.
 */
public class TalentAttrs {
    private Talent talentTarget; //
    private Map<HeroAttrsRange, HeroAttrs> rangeToHeroAttrs = new HashMap<>(); // 天赋点增加的属性

    public TalentAttrs(Talent talentTarget) {
        this.talentTarget = talentTarget;
    }


    public void calcuTalentAttrs() {
        if (talentTarget == null) {
            return;
        }
        rangeToHeroAttrs.clear();
        rangeToHeroAttrs.putAll(HeroAttrsUtils.calcuTalentAttrs(this.talentTarget));
    }


    public void mergeAllFixAttrs(HeroAttrs heroAttrs, SystemModuleTypeEnum moduleType, Hero belongHero, Formation formation) {
        mergeAllAttrs(heroAttrs, true, moduleType, belongHero, formation);
    }

    public void mergeAllPrecentAttrs(HeroAttrs heroAttrs, SystemModuleTypeEnum moduleType, Hero belongHero, Formation formation) {
        mergeAllAttrs(heroAttrs, false, moduleType, belongHero, formation);
    }

    public void mergeAllAttrs(HeroAttrs heroAttrs, boolean isFiex, SystemModuleTypeEnum moduleType, Hero belongHero, Formation formation) {
        if (this.talentTarget == null || formation == null) {
            return;
        }
        rangeToHeroAttrs.forEach((range, attrs) -> {
            if (HeroAttrsUtils.meetFightTarget(range, belongHero, formation) && HeroAttrsUtils.meetFightCondition(range, belongHero)) {
                if (range.getModuleType() == SystemModuleTypeEnum.MODULE_ALL) {
                    HeroAttrsUtils.mergeAttrs(heroAttrs, attrs, isFiex);
                } else {
                    if (range.getModuleType() == moduleType) {
                        HeroAttrsUtils.mergeAttrs(heroAttrs, attrs, isFiex);
                    }
                }
            }
        });
    }


    public void setTalentTarget(Talent talentTarget) {
        this.talentTarget = talentTarget;
    }
}
