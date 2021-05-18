package ws.analogClient.features.flow.functions.guild.trainer;

import ws.analogClient.features.flow.functions.guild.base.Func_Guild_Utils;

/**
 * Created by lee on 6/5/17.
 */
public class Func_Guild_Trainer {
    public static void execute() {
        Func_Guild_Utils.createGuild();

    }

    public static void test1() {
        Func_Guild_Utils.createGuild();
        Func_Guild_Trainer_Utils.sync();
    }

    public static void test2() {
        Func_Guild_Utils.createGuild();
        Func_Guild_Trainer_Utils.sync();
        Func_Guild_Trainer_Utils.unlock(1);
        Func_Guild_Trainer_Utils.sync();
    }

    public static void test3() {
        Func_Guild_Utils.createGuild();
        Func_Guild_Trainer_Utils.sync();
        Func_Guild_Trainer_Utils.unlock(1);
        Func_Guild_Trainer_Utils.sync();
        Func_Guild_Trainer_Utils.replace(100000001, 1);
        Func_Guild_Trainer_Utils.sync();
    }

    public static void test4() {
        Func_Guild_Utils.join("599a442908e412053850b9b9");
        Func_Guild_Trainer_Utils.accelerate("599e93f908e41256af23384e", 1);
    }


    public static void test5() {
        Func_Guild_Trainer_Utils.stamp("599cefeb08e4126461365347");
    }

    public static void test6() {
        Func_Guild_Trainer_Utils.getTrainerInfo("599cef8d08e4126461363fb3");
    }

}
