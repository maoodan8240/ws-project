package ws.particularFunctionServer.utils.codeHelper.tabToJava;

import ws.common.table.data.PlanningTableData;
import ws.common.utils.dataSource.txt._PlanningTableData;
import ws.common.utils.fileHandler.FileCharacters;
import ws.common.utils.fileHandler.FileWriteCharacters;
import ws.common.utils.fileHandler.FileWriter;
import ws.common.utils.fileHandler._FileReadWritArgs;
import ws.particularFunctionServer.system.GlobalData;
import ws.particularFunctionServer.system.config.AppConfig;
import ws.particularFunctionServer.system.di.GlobalInjectorUtils;
import ws.particularFunctionServer.system.table.RootTcListener;
import ws.relationship.table.RootTc;
import ws.relationship.table.tableRows.Table_ServerConfig_Row;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GenServerConfig {
    private static final String PATH = "/home/lee/project/relationship/src/main/java/ws/relationship/table/";
    private static PlanningTableData planningTableData = null;

    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        planningTableData = new _PlanningTableData(AppConfig.getString(AppConfig.Key.WS_Common_Config_planningTableData_tab_file_path));
        planningTableData.loadAllTablesData();

        RootTc.init(planningTableData, new RootTcListener());
        RootTc.loadAllTables(GlobalData.getLocale());

        String content = "package  ws.relationship.table;  import SendSynchronizedMsgFailedException; import ServerRoleAddressNotFoundException; import ws.relationship.table.tableRows.Table_ServerConfig_Row; import ws.common.table.table.interfaces.table.Table;  public class AllServerConfig {      %s     \n\n private int id;      public AllServerConfig(int id) {         this.id = id;     }      public <T> T getConfig() {         return RootTc.get(Table_ServerConfig_Row.class).get(id).getConfig();     } public int getId() { return id; } } ";
        List<Table_ServerConfig_Row> rows = RootTc.get(Table_ServerConfig_Row.class).values();
        StringBuffer sb = new StringBuffer();
        String rowStr = "/**\n* %s\n*/\n public static final AllServerConfig %s = new AllServerConfig(%s);";
        for (Table_ServerConfig_Row row : rows) {
            sb.append(String.format(new String(rowStr), row.getDesc(), row.getSimpleName(), row.getId())).append("\n");
        }
        File file = new File(PATH + "AllServerConfig.java");
        FileWriteCharacters characters = new FileWriteCharacters(file, String.format(content, sb.toString()));
        characters.setAppendText(false);
        FileWriter.write(new WriteServerConfig(characters));
    }

    static class WriteServerConfig extends _FileReadWritArgs {

        public WriteServerConfig(FileCharacters fileCharacters) {
            super(fileCharacters);
        }

        @Override
        public void callContentWrite(BufferedWriter bufWrite, String content) {
            try {
                bufWrite.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
