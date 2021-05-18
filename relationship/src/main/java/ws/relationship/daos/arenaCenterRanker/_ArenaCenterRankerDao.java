package ws.relationship.daos.arenaCenterRanker;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.QueryOperators;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import ws.common.mongoDB.handler.PojoHandler;
import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.common.mongoDB.utils.WsJsonUtils;
import ws.relationship.topLevelPojos.pvp.arenaCenter.ArenaCenterRanker;

import java.util.ArrayList;
import java.util.List;

public class _ArenaCenterRankerDao extends AbstractBaseDao<ArenaCenterRanker> implements ArenaCenterRankerDao {

    public _ArenaCenterRankerDao() {
        super(ArenaCenterRanker.class);
    }


    @Override
    public ArenaCenterRanker queryByRank(int rank) {
        Document doc = new Document("rank", rank);
        return findOne(doc);
    }

    public List<ArenaCenterRanker> queryRankTopN_NotRobot(int minRank) {
        Document doc = new Document();
        doc.append("robot", false);
        Document lte = new Document(QueryOperators.LTE, minRank);
        doc.append("rank", lte);
        return findAll(doc);
    }

    @Override
    public void mutliReplace(List<ArenaCenterRanker> rankerList) {
        List<WriteModel<Document>> writeModelList = new ArrayList<>();
        for (ArenaCenterRanker ranker : rankerList) {
            // Document iDDocument = getIDDocument(ranker);
            JSONObject jsonObject = WsJsonUtils.javaObjectToJSONObject(ranker);
            Document iDDocument = new Document("_id", new ObjectId(jsonObject.getString("_id")));
            Document objDoc = PojoHandler.pojoToDocument(ranker);
            ReplaceOneModel<Document> oneReplace = new ReplaceOneModel<>(iDDocument, objDoc, new UpdateOptions().upsert(true));
            writeModelList.add(oneReplace);
        }
        getMongoCollection().bulkWrite(writeModelList);
    }
}
