/**
 * Created by zhangweiwei on 17-6-8.
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

    public static void main(String[] args) {
        Sut ss = null;
        test(ss);
        System.out.println(ss);
    }

    private static void test(Sut sut) {
        Sut s = new Sut("lee", 11);
        sut = s;
        System.out.println("test:" + sut);

    }
}