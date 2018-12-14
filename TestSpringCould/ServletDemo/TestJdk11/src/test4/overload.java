package test4;

public class overload {

    public void get() {
        System.out.println("null");
    }

    public void get(String a) {
        System.out.println(a);
    }

    public int get(int a) {
        return a;
    }

    public static void main(String[] args) {
        String a = "h";
        String b = a + "e";
        StringBuilder c = new StringBuilder(b);
        StringBuilder d = c.append("hello").append("world");
        System.out.println(d);
    }
}
