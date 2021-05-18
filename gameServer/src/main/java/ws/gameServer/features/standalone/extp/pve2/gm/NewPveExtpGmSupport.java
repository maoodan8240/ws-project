package ws.gameServer.features.standalone.extp.pve2.gm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.gameServer.features.standalone.extp.pve2.ctrl.NewPveCtrl;
import ws.protos.EnumsProtos.MapTypeEnum;
import ws.relationship.base.MagicNumbers;
import ws.relationship.enums.GmCommandFromTypeEnum;
import ws.relationship.gm.GmCommand;
import ws.relationship.gm.GmCommandGroupNameConstants.PveGmSupport;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_Maps_Row;
import ws.relationship.table.tableRows.Table_Pve_Row;
import ws.relationship.topLevelPojos.newPve.Chapter;
import ws.relationship.topLevelPojos.newPve.NewStage;

import java.util.List;

/**
 * Created by zhangweiwei on 17-6-14.
 */
public class NewPveExtpGmSupport implements GmCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewPveExtpGmSupport.class);
    private NewPveCtrl newPveCtrl;

    public NewPveExtpGmSupport(NewPveCtrl newPveCtrl) {
        this.newPveCtrl = newPveCtrl;
    }

    @Override
    public void exec(GmCommandFromTypeEnum fromTypeEnum, String commandName, String[] args) {
        if (PveGmSupport.OpenOneChapter.getStr().equals(commandName)) {
            onOpenOneChapter(fromTypeEnum, args);
        } else if (PveGmSupport.OpenAllChapter.getStr().equals(commandName)) {
            onOpenAllChapter(fromTypeEnum, args);
        }
    }

    private void onOpenAllChapter(GmCommandFromTypeEnum fromTypeEnum, String[] args) {
        List<Table_Maps_Row> rows = RootTc.get(Table_Maps_Row.class).values();
        for (Table_Maps_Row mapsRow : rows) {
            _openOneChapter(mapsRow);
        }
        newPveCtrl.sync();
    }


    private void onOpenOneChapter(GmCommandFromTypeEnum fromTypeEnum, String[] args) {
        int mapId = Integer.valueOf(args[0]);
        if (RootTc.has(Table_Maps_Row.class, mapId)) {
            Table_Maps_Row mapsRow = RootTc.get(Table_Maps_Row.class, mapId);
            _openOneChapter(mapsRow);
        }
        newPveCtrl.sync();
    }

    private void _openOneChapter(Table_Maps_Row mapsRow) {
        int mapId = mapsRow.getId();
        MapTypeEnum mapType = MapTypeEnum.valueOf(mapsRow.getMapType());
        Chapter chapter = newPveCtrl.getChapter(mapId);
        int sumStar = chapter.getChapterSumStar();
        if (mapType == MapTypeEnum.MAP_ELITE || mapType == MapTypeEnum.MAP_SIMPLE) {
            List<Table_Pve_Row> rows = RootTc.get(Table_Pve_Row.class).values();
            for (Table_Pve_Row row : rows) {
                if (row.getMapId() == mapId) {
                    NewStage stage = newPveCtrl.getNewStage(row.getId());
                    stage.setMaxStar(MagicNumbers.PVE_STAGE_MAX_STAR_LEVEL);
                    sumStar += stage.getMaxStar();
                }
            }
        }
        chapter.setChapterSumStar(sumStar);
        newPveCtrl.save();
    }
}
