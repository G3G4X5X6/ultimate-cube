public class TestRegx {
    public static void main(String[] args) {
        // The first regex evaluation will never end in JDK <= 9
        // and the second regex evaluation will never end in any versions of the JDK:

//        java.util.regex.Pattern.compile("(a+)+").matcher(
//                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+
//                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+
//                        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+
//                        "aaaaaaaaaaaaaaa!").matches(); // Sensitive

        java.util.regex.Pattern.compile("(h|h|ih(((i|a|c|c|a|i|i|j|b|a|i|b|a|a|j))+h)ahbfhba|c|i)*").matcher(
                "hchcchicihcchciiicichhcichcihcchiihichiciiiihhcchi"+
                        "cchhcihchcihiihciichhccciccichcichiihcchcihhicchcciicchcccihiiihhihihihi"+
                        "chicihhcciccchihhhcchichchciihiicihciihcccciciccicciiiiiiiiicihhhiiiihchccch"+
                        "chhhhiiihchihcccchhhiiiiiiiicicichicihcciciihichhhhchihciiihhiccccccciciihh"+
                        "ichiccchhicchicihihccichicciihcichccihhiciccccccccichhhhihihhcchchihih"+
                        "iihhihihihicichihiiiihhhhihhhchhichiicihhiiiiihchccccchichci").matches(); // Sensitive

        System.out.println("Over!");
    }
}
