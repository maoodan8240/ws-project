package ws.relationship.daos.guild;

import org.bson.Document;
import ws.common.mongoDB.implement.AbstractBaseDao;
import ws.relationship.topLevelPojos.newGuildCenter.NewGuild;

/**
 * Created by lee on 6/2/17.
 */
public class _NewGuildDao extends AbstractBaseDao<NewGuild> implements NewGuildDao {
    public _NewGuildDao() {
        super(NewGuild.class);
    }

    public NewGuild queryNewGuild(String guildId) {
        Document document = new Document("guildId", guildId);
        return findOne(document);
    }

    public NewGuild queryNewGuildByName(String guildName) {
        Document document = new Document("guildName", guildName);
        return findOne(document);
    }

    @Override
    public NewGuild queryNewGuildBySimpleId(int simpleId) {
        Document document = new Document("simpleId", simpleId);
        return findOne(document);
    }
}
