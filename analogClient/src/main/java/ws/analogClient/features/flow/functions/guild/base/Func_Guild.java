package ws.analogClient.features.flow.functions.guild.base;

import ws.protos.EnumsProtos.GuildJobEnum;
import ws.protos.EnumsProtos.GuildJoinTypeEnum;

/**
 * Created by lee on 17-4-12.
 */
public class Func_Guild {
    public static void execute() {
        Func_Guild_Utils.sync();
        test1();
        // test3();
        // test9();
        // test4();
        // test5();
        // test6();
        // test7();
        // test14();
        // test9();
        // test10();
        // test12();
        // test8();
        // test5();
        // test11();
        // test13();
        // Func_Guild_Utils.sync();

    }

    private static void test1() {
        Func_Guild_Utils.create("社团0001", 101);
    }

    private static void test2() {
        Func_Guild_Utils.search1("111");
    }

    public static void test3() {
        Func_Guild_Utils.join("592f7ed808e41238da4ab460");
    }

    public static void test4() {
        Func_Guild_Utils.sync();
    }

    public static void test5() {
        Func_Guild_Utils.search(0, 0);
    }

    public static void test6() {
        Func_Guild_Utils.agree("592f81ba08e4123ee50a92ab");
    }

    public static void test7() {
        Func_Guild_Utils.getApply(0, 4);
    }

    public static void test8() {
        Func_Guild_Utils.getMember(0, 3);
    }

    public static void test9() {
        Func_Guild_Utils.change(GuildJoinTypeEnum.GJ_APPLY, 25);
    }

    public static void test10() {
        Func_Guild_Utils.appoint("5927ee6f08e41208f9e83f15", GuildJobEnum.GJ_MASTER);
    }

    public static void test11()

    {
        Func_Guild_Utils.out();
    }

    public static void test12() {
        Func_Guild_Utils.kick("592816ef08e4124719b300bf");
    }

    public static void test13() {
        Func_Guild_Utils.disband();
    }

    public static void test14() {
        Func_Guild_Utils.giveJob("592e927a08e4120792355273");
    }
}
