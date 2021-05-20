package test;

import ws.particularFunctionServer.Launcher;
import ws.particularFunctionServer.system.config.AppConfig;
import ws.particularFunctionServer.system.di.GlobalInjectorUtils;
import ws.relationship.topLevelPojos.mailCenter.MailsCenter;
import ws.relationship.utils.InitCommonCreatedTargetsDB;

/**
 * Created by lee on 17-5-23.
 */
public class Test_InitCommonCreatedTargetsDB {


    public static void main(String[] args) throws Exception {
        AppConfig.init();
        GlobalInjectorUtils.init();
        Launcher.mongodbInit();

        InitCommonCreatedTargetsDB.init();
        boolean rs2 = InitCommonCreatedTargetsDB.containsTopLevelPojo(MailsCenter.class.getSimpleName());
        System.out.println(rs2);

        InitCommonCreatedTargetsDB.update(MailsCenter.class.getSimpleName());
        boolean rs1 = InitCommonCreatedTargetsDB.containsTopLevelPojo(MailsCenter.class.getSimpleName());
        System.out.println(rs1);
    }
}
