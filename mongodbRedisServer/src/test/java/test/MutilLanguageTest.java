package test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by lee on 17-6-7.
 */
public class MutilLanguageTest {


    public static void main(String[] args) {
        Locale locale = Locale.CHINA;
        ResourceBundle bundle = ResourceBundle.getBundle("xxx", locale);
        System.out.println(bundle.getBaseBundleName());
        if (bundle.containsKey("aaa")) {
            System.out.println(bundle.getString(""));
        }
    }
}
