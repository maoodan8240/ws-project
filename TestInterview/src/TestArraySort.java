public class TestArraySort {
    public static void main(String[] args) {
        int arr[] = {2, 5, 4, 6, 3, 8, 7, 9, 1};
        final int N = 1000000;

        int[] ints1 = new int[100];

        for (int i = 0; i < ints1.length; i++) {
            ints1[i] = i;
        }
        TestShuffleArr.shuffle(ints1);
        int[] ints2 = ints1.clone();
        int[] ints3 = ints1.clone();
        int[] ints4 = ints1.clone();
        int[] ints5 = ints1.clone();
        // System.out.println("=============================");
        // sout(ints1);
        // System.out.println("=============================");
        // sout(ints2);
        // System.out.println("=============================");
        // sout(ints3);
        // System.out.println("=============================");
        // sout(ints4);
        // System.out.println("=============================");


        long l1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            int low = 0;
            int hight = ints1.length - 1;
            quickSort(ints1, low, hight);
        }
        System.out.println("quicksort:" + (System.currentTimeMillis() - l1));

        // (l1 = System.currentTimeMillis();
        //         // int low1 = 0;
        //         // int hight2 = ints2.length - 1;
        //         // mergeSort(ints2, low1, hight2);
        //         // System.out.println("mergeSort:" + (System.currentTimeMillis() - l1));
        //         // // sout(arr);
        //         // l1 = System.currentTimeMillis();
        //         // Arrays.sort(ints3);
        //         // System.out.println"Arrays.sort:" + (System.currentTimeMillis() - l1));
        // l1 = System.currentTimeMillis();
        // bubbleSort(ints2);
        // System.out.println("bubbleSort:" + (System.currentTimeMillis() - l1));
        // l1 = System.currentTimeMillis();
        // selectSort(ints3);
        // System.out.println("selectSort:" + (System.currentTimeMillis() - l1));
        l1 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            inserSort(ints4);
        }
        System.out.println("inserSort:" + (System.currentTimeMillis() - l1));

        // sout(ints);
        // System.out.println("=============================");
        // sout(ints1);
        // System.out.println("=============================");
        // sout(ints2);
        // System.out.println("=============================");
        // sout(ints3);
        // System.out.println("=============================");
        // sout(ints4);
        // System.out.println("=============================");
    }

    //归并排序
    public static void mergeSort(int[] arr, int start, int end) {
        //判断拆分的不为最小单位
        if (end - start > 0) {
            //再一次拆分，知道拆成一个一个的数据
            mergeSort(arr, start, (start + end) / 2);
            mergeSort(arr, (start + end) / 2 + 1, end);
            //记录开始/结束位置
            int left = start;
            int right = (start + end) / 2 + 1;
            //记录每个小单位的排序结果
            int index = 0;
            int[] result = new int[end - start + 1];
            //如果查分后的两块数据，都还存在
            while (left <= (start + end) / 2 && right <= end) {
                //比较两块数据的大小，然后赋值，并且移动下标
                if (arr[left] <= arr[right]) {
                    result[index] = arr[left];
                    left++;
                } else {
                    result[index] = arr[right];
                    right++;
                }
                //移动单位记录的下标
                index++;
            }
            //当某一块数据不存在了时
            while (left <= (start + end) / 2 || right <= end) {
                //直接赋值到记录下标
                if (left <= (start + end) / 2) {
                    result[index] = arr[left];
                    left++;
                } else {
                    result[index] = arr[right];
                    right++;
                }
                index++;
            }
            //最后将新的数据赋值给原来的列表，并且是对应分块后的下标。
            for (int i = start; i <= end; i++) {
                arr[i] = result[i - start];
            }
        }
    }


    //选择排序
    public static void selectSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            //假设第一个值就最小的
            int min = arr[i];
            //记录最小的坐标
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                //比较数组里有没有比假设最小的还小的值
                if (min > arr[j]) {
                    //小的话,把最小值设置到min里
                    min = arr[j];
                    //最小的角标也更新成j的
                    minIndex = j;
                }

            }
            //做交换,先把i的值保存好
            int temp = arr[i];
            //把i换成min,此时min是刚才j的值
            arr[i] = min;
            //把最小角标也就是j换成刚才存好的i的值,完成调换
            arr[minIndex] = temp;
        }
    }

    //冒泡排序
    public static void bubbleSort(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            //遍历次数
            for (int j = 0; j < arr.length - 1; j++) {
                //看相邻的两个元素大小,小的放前面,大的放后面
                if (arr[j] > arr[j + 1]) {
                    //交换位置
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }


    //插入排序
    public static void inserSort(int arr[]) {
        //外层循环i从1开始,也就是第二个开始比较
        for (int i = 1; i < arr.length; i++) {
            //内循环 j是后面的数据, j-1是前面的数据,如果后面的数据比前面的小,那就把后面的数据插到前面(也就换个位置)
            //插入和冒泡很像,但在这里不同了,插入在内层遍历的起点是有外层i的增长决定的,也就是指对比后面无序的,前面的已经排好了
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    int temp = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = temp;
                } else {
                    break;
                }
            }


        }
    }



    //快速排序
    public static void quickSort(int arr[], int low, int hight) {
        if (hight - low < 1) {
            return;
        }

        int start = low;
        int end = hight;
        boolean flag = true;//true高指针,false低指针

        int midValue = arr[low];

        while (true) {
            if (flag) {
                //高指针的值比中间值大,属于正常左小右大的顺序,接着往中间移动指针
                if (arr[hight] > midValue) {
                    hight--;
                } else if (arr[hight] < midValue) {
                    //目的,把高指针一侧比中间值大的数据往第一指针一侧挪动
                    //比中间值小,高指针的值覆盖低指针的值,arr的low角标值处理完了,将low指针往中间移动, 并将指针交给低指针
                    arr[low] = arr[hight];
                    low++;
                    flag = false;
                }
            } else {
                //低指针比中间值小,属于正常左小右大的顺序,指针接着往中间移动
                if (arr[low] < midValue) {
                    low++;
                } else if (arr[low] > midValue) {
                    //目的和高指针相反,把这边比中间值大的数往高指针那边挪
                    //比中间值大,低指针的值覆盖高指针的值,arr的第hight个角标处理完了,将hight指针往中间移动, ,将指针交给高指针
                    arr[hight] = arr[low];
                    hight--;
                    flag = true;
                }
            }
            // arr[low] = arr[hight];arr[hight] = arr[low];
            // 这两步操作看似是覆盖逻辑,不会丢失数据,其实是通过midValue做了交换,midValue充当了temp临时变量
            // 当两个指针相遇时,再将中间值还给低指针的值
            if (low == hight) {
                arr[low] = midValue;
                break;
            }
        }

        //start是0, 从start到low-1看着成是arr左半边,
        //low+到end是arr的右半边,分别在用上面的逻辑再排序一遍
        quickSort(arr, start, low - 1);
        quickSort(arr, low + 1, end);

    }

    public static void sout(int arr[]) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {

            System.out.print(arr[i]);
            if (i != arr.length - 1)
                System.out.print(",");
        }
        System.out.print("]");
    }
}
