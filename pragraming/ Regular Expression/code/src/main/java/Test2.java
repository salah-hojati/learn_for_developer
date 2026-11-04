public class Test2 {
    public static void main(String[] args) {

        String phone = "^021-?\\d{8}$";
        System.out.println("02112345678".matches(phone));
        System.out.println("021-12345678".matches(phone));



        String bad = "^\\d+$";
        System.out.println("".matches(bad));       // ?
        System.out.println("12345".matches(bad));  // ?
    }
}
