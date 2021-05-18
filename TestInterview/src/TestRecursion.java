

public class TestRecursion {


    public static void main(String[] args) {
        System.out.println(func(30));
    }

    public static int func(int i) {
        if (i < 0)
            return 0;
        else if (i > 0 && i <= 2)
            return 1;
        else return func(i - 1) + func(i - 2);
    }

}
