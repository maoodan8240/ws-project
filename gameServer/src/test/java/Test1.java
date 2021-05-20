import org.junit.Test;
import ws.gameServer.features.standalone.extp.utils.UpgradeLevel;
import ws.relationship.topLevelPojos.common.LevelUpObj;

/**
 * Created by lee on 17-6-8.
 */
public class Test1 {

    static class Sut {
        String name;
        int age;

        public Sut(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return this.name + "," + this.age;
        }
    }


    private static void test(Sut sut) {
        Sut s = new Sut("lee", 11);
        sut = s;
        System.out.println("test:" + sut);

    }

    @Test
    public void name() {
        LevelUpObj obj1 = new LevelUpObj();
        UpgradeLevel.levelUp(obj1, 1000, 100, new UpgradeLevel.NextLevelNeedExp() {
            @Override
            public int getExp(int oldLevel) {
                return 100;
            }
        });
        System.out.println(obj1.getLevel());
        System.out.println(obj1.getOvfExp());
    }

   
}