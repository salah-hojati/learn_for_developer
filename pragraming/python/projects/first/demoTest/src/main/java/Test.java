public class Test {

    public static void main(String[] args) {
        System.out.println("this is a book. \n  new line");
        System.out.println(countWord("this is a book. \n  new line"));
         System.out.println(countWord(""));
//
    }

    //
    private static int  countWord(String text){
       // text.replace("\n")
        if(text!=null && !text.isEmpty())
       return text.split("\\s+").length;
        else return 0;

    }
}
