public class Transfers {
    public static void main(String[] args) throws Exception {
        Record r1 = new Record();
        Record r2 = new Record();
        doTransfer(r1, r2, 5);
        doTransfer(r2, r1, 2);
        doTransfer(r1, r2, 1);
        System.out.println("rl = " + r1.get() + ", r2=" + r2.get());
    }

    private static void doTransfer(final Record a, final Record b, final int amount) {
        Thread t = new Thread() {
            public void run() {
                new Clerk().transfer(a, b, amount);
            }
        };
        t.start();
    }
}

class Clerk {
    public synchronized void transfer(Record a, Record b, int amount) {
        synchronized (a) {
            synchronized (b) {
                a.add(-amount);//5,
                b.add(amount);//15,
            }
        }
    }
}

class Record {
    int num = 10;
    String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName(String name) {
        return this.name;
    }

    public int get() {
        return num;
    }

    public void add(int n) {
        num = num + n;
    }
}
