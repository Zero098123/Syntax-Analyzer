package utils;

import java.util.regex.Pattern;

public class NumberUtils {

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]+");
        if (!pattern.matcher(str).matches()) {
            return false;
        }

        int i = Integer.parseInt(str);
        if (i >= 0 && i <= 32767) {
            return true;
        } else {
            return false;
        }
    }
}
