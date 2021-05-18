package ws.relationship.base.msg.heros;

import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.newBattle.NewBattleHeroContainer;
import ws.protos.EnumsProtos.FormationTypeEnum;

/**
 * Created by zhangweiwei on 17-5-24.
 */
public class In_QueryBattleHeroContainerInFormation {

    public static class Request extends AbstractInnerMsg {
        private static final long serialVersionUID = 7625894144863926416L;
        private FormationTypeEnum type;
        private int outerRealmId;
        private String playerId;  // 被查询的人

        public Request(FormationTypeEnum type, int outerRealmId, String playerId) {
            this.type = type;
            this.outerRealmId = outerRealmId;
            this.playerId = playerId;
        }

        public String getPlayerId() {
            return playerId;
        }

        public FormationTypeEnum getType() {
            return type;
        }

        public int getOuterRealmId() {
            return outerRealmId;
        }
    }


    public static class Response extends AbstractInnerMsg {
        private static final long serialVersionUID = 6340756399686487288L;
        private NewBattleHeroContainer container;

        public Response(NewBattleHeroContainer container) {
            this.container = container;
        }

        public NewBattleHeroContainer getContainer() {
            return container;
        }
    }
}
