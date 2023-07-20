package dev.tenacity.utils.ui.render;

public class StringConversions {
    public static Object castNumber(String newValueText) {
        if (newValueText.contains(".")) {
            if (newValueText.toLowerCase().contains("f")) {
                return Float.parseFloat(newValueText);
            }
            return Double.parseDouble(newValueText);
        }
        if (isNumeric(newValueText)) {
            return Integer.parseInt(newValueText);
        }
        return newValueText;
    }

    public static boolean isNumeric(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }
}
