package ws.particularFunctionServer.utils.codeHelper.tabToJava;

import org.apache.commons.lang3.StringUtils;
import ws.common.table.data.PlanningTableData;
import ws.common.table.data.TableData;
import ws.common.utils.dataSource.txt._PlanningTableData;
import ws.particularFunctionServer.system.config.AppConfig;
import ws.relationship.base.MagicWords;

import java.io.File;
import java.io.IOException;

public class MakeCodeFile {
    private static final String PATHROOT = "/home/lee/project/particularFunctionServer/src/main/java/";
    private static PlanningTableData planningTableData = null;

    public static void main(String[] args) throws Exception {
        AppConfig.init();
        planningTableData = new _PlanningTableData(AppConfig.getString(AppConfig.Key.WS_Common_Config_planningTableData_tab_file_path));
        planningTableData.loadAllTablesData();

        String tableName = "Table_RoleExp.tab";
        String newFileName = "";
        if (StringUtils.isEmpty(newFileName)) {
            newFileName = tableName.replace(".tab", MagicWords.TableFileNameGenSuffix);
        }
        tableName = tableName.replace(".tab", "");
        print(tableName, "ws.particularFunctionServer.features.rows", newFileName);
    }

    public static void print(String tableName, String packagePath, String newFileName) throws IOException {
        TableData tableDataTxt = planningTableData.getTableNameToTableData().get(tableName);
        StringBuffer sb = GenTableRowAccesserCodeFile.printCode(tableDataTxt, packagePath, newFileName);
        String filePath = packagePath.replace(".", "/");
        File file = new File(PATHROOT + filePath + "/" + newFileName + ".java");
        System.out.println(file);
        if (file.exists() && file.isFile()) {
            GenTableRowAccesserCodeFile.clearFile(file);
        } else {
            file.createNewFile();
        }
        GenTableRowAccesserCodeFile.writeToFile(file, sb.toString(), true);
    }
}
