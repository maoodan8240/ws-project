package ws.relationship.utils.attrs;

import ws.protos.EnumsProtos.FightTargetConditonEnum;
import ws.protos.EnumsProtos.FightTargetEnum;
import ws.protos.EnumsProtos.SystemModuleTypeEnum;

/**
 * Created by zhangweiwei on 17-4-27.
 */
public class HeroAttrsRange {
    private SystemModuleTypeEnum moduleType;    // 限制的玩法
    private FightTargetEnum fightTarget;        // 限制的目标
    private FightTargetConditonEnum conditon;   // 限制的目标条件


    public HeroAttrsRange(FightTargetEnum fightTarget, FightTargetConditonEnum conditon) {
        this(SystemModuleTypeEnum.MODULE_ALL, fightTarget, conditon);
    }

    public HeroAttrsRange(SystemModuleTypeEnum moduleType, FightTargetEnum fightTarget, FightTargetConditonEnum conditon) {
        this.moduleType = moduleType;
        this.fightTarget = fightTarget;
        this.conditon = conditon;
    }

    public FightTargetEnum getFightTarget() {
        return fightTarget;
    }

    public FightTargetConditonEnum getConditon() {
        return conditon;
    }

    public SystemModuleTypeEnum getModuleType() {
        return moduleType;
    }
}
