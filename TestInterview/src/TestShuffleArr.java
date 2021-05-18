import java.util.Random;

public class TestShuffleArr {

    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};

        shuffle2(arr);
    }

    public static void shuffle2(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1; j++) {
                int temp = arr[j];
                if (j > 0) {
                    if (temp % 2 != 0) {
                        arr[j] = arr[j - 1];
                        arr[j - 1] = temp;
                    } else {
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            }
        }

        for (int j = 0; j < arr.length; j++) {
            System.out.print(arr[j]);
        }
    }

    public static void shuffle(int arr[]) {
        int i = arr.length - 1;
        Random r = new Random();
        while (i > 0) {
            int j = r.nextInt(i);
            if (arr[j] != arr[i]) {
                int temp = arr[j];
                arr[j] = arr[i];
                arr[i] = temp;
                i--;
            }
        }

    }

}
