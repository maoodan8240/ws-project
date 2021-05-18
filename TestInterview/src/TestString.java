import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestString {
    public static void main(String[] args) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            Item item = new Item(i * 10);
            list.add(item);
        }
        int weightSum = 0;
        for (Item item : list) {
            weightSum += item.getWeight();
        }
        if (weightSum == 0) {
            return;
        }
        Random r = new Random();
        int seed = r.nextInt(weightSum);
        int reslut = 0;
        for (Item item : list) {
            if (reslut <= seed && seed < reslut + item.getWeight()) {
                System.out.println(item.getWeight());
                return;
            }
            reslut += item.getWeight();
        }
    }

    static class Item {
        int weight;


        public Item(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return this.weight;
        }


    }
}
