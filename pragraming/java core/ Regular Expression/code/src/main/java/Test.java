public class Test {
    public static void main(String[] args) {

        String lowCahr="^[a-z]+$";
        System.out.println("adc".matches(lowCahr));

        String ninCode="^\\d{8}$";
        System.out.println("12345678".matches(ninCode));

        String iranMobile="^[0][9]\\d{9}$";
        System.out.println("09123456789".matches(iranMobile));

        String postalCode="^[0-9-]{10}$";
        System.out.println("1234567890".matches(postalCode));
        System.out.println("2345-67890".matches(postalCode));

        String user ="^[A-Za-z0-9_]{3,16}";
        System.out.println("ali123_ali".matches(user));
    }
}
