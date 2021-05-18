public class TestRemoveDuplicates {
    public static void main(String[] args) {
        String s = "aababaab";
        // String news = s.replace("bb", "");
        String news = removeDuplicates1(s);
        System.out.println(news);
    }

    public static String removeDuplicates(String S) {
        if (S == null || S.equals("") || S.length() <= 2) {
            return S;
        }
        String news = S;
        char[] strArr = news.toCharArray();
        String str = "";
        for (int i = 0; i < strArr.length - 1; i++) {
            char temp = strArr[i];
            for (int k = i + 1; k < strArr.length; k++) {
                if (temp == strArr[k]) {
                    str = str + strArr[k];
                } else {
                    break;
                }

            }
            if (str.length() != 0) {
                str = str + temp;
                news = news.replace(str, "");
                return removeDuplicates(news);
            }
        }
        return news;


    }


    public static String removeDuplicates1(String S) {
        if (S == null || S.equals("")) {
            return S;
        }
        char[] strArr = S.toCharArray();
        char[] newArr = new char[8];
        for (int i = 0; i < strArr.length; i++) {
            if (i < strArr.length - 1) {
                if (strArr[i] == (strArr[i + 1])) {
                    newArr[i] = strArr[i];
                    newArr[i + 1] = strArr[i + 1];
                }
            } else {
                if (new String(newArr).trim().length() == 0) {
                    return S;
                }
            }
        }
        String ss = new String(newArr).trim();
        String news = S.replace(ss, "");
        return removeDuplicates(news);
    }

}
