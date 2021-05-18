public class TestPrint {
    public static void main(String[] args) {
        function(5);
    }

    private static void function(int i) {
        System.out.println("check int:" + i);
        if (i > 0) {
            function(i - 1);
        }
        System.out.println("commit int :" + i);
    }


}
