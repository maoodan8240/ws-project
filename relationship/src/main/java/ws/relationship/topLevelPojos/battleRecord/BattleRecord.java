package ws.relationship.topLevelPojos.battleRecord;

import com.alibaba.fastjson.annotation.JSONField;
import ws.common.mongoDB.interfaces.TopLevelPojo;

/**
 * Created by zhangweiwei on 17-5-25.
 */
public class BattleRecord implements TopLevelPojo {
    private static final long serialVersionUID = 4275644207326399135L;
    @JSONField(name = "_id")
    private String recordId;
    private String script;

    public BattleRecord() {
    }

    public BattleRecord(String recordId, String script) {
        this.recordId = recordId;
        this.script = script;
    }

    @Override
    public String getOid() {
        return recordId;
    }

    @Override
    public void setOid(String oid) {
        this.recordId = oid;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
