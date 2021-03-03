package dev.zomo.MCLang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class LangTemplate {

    private HashMap<String, LangTemplateValue> values = new HashMap<String, LangTemplateValue>();

    /**
     * Constructor for the LangTemplate object
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     */
    public LangTemplate() {}

    /**
     * Add a value to the template
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param key   name of template value
     * @param value value for template key
     */
    private LangTemplate add(String key, LangTemplateValue value) {

        values.put(key, value);

        return this;
    }

    /**
     * Add a value to the template
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param key   name of template value
     * @param value value for template key
     */
    public LangTemplate add(String key, String value) {
        return add(key, new LangTemplateValue(value));
    }

    /**
     * Add a value to the template
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param key   name of template value
     * @param value value for template key
     */
    public LangTemplate add(String key, int value) {
        return add(key, new LangTemplateValue(value));
    }

    /**
     * Add a value to the template
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param key   name of template value
     * @param value value for template key
     */
    public LangTemplate add(String key, Boolean value) {
        return add(key, new LangTemplateValue(value));
    }

    /**
     * Get the value of the key
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param key name of template value
     */
    private LangTemplateValue get(String key) {

        /*for (int i = 0; i < arraylength; i++) {
            if (keys.get(i).equals(key))
                return values.get(i);
        }*/

        if (values.containsKey(key))
            return values.get(key);

        return new LangTemplateValue();

    }

    /**
     * Create a string from the template
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     */
    public String apply(String str) {
        List<String> arr = Arrays.asList(str.split(""));

        String ret = "";

        boolean readName = false;
        boolean escape = false;
        boolean compareTo = false;
        boolean compareTrue = false;
        boolean compareFalse = false;

        String templateString = "";
        String compareToMode = "";
        String compareToString = "";
        String compareTrueString = "";
        String compareFalseString = "";

        for (int i = 0; i < arr.size(); i++) {

            if (readName) { //reading template name between {}

                if (escape) { // escape character before
                    escape = false;
                    //templateString += arr.get(i);

                    if (compareTo)
                        compareToString += arr.get(i);
                    else if (compareTrue)
                        compareTrueString += arr.get(i);
                    else if (compareFalse)
                        compareFalseString += arr.get(i);
                    else
                        templateString += arr.get(i);

                } else if (arr.get(i).equals("\\")) { // escape character
                    escape = true;
                } else if (arr.get(i).equals("}")) { // end
                    readName = false;
                    compareTo = false;
                    compareTrue = false;
                    compareFalse = false;

                    //no comparisons at all
                    if (compareToMode.length() == 0 && compareToString.length() == 0 && compareTrueString.length() == 0 && compareFalseString.length() == 0) {
                        ret += this.get(templateString).value;
                    } else {
                        boolean comparison = false;

                        LangTemplateValue leftVal = this.get(templateString);
                        LangTemplateValue rightVal = null;

                        //type of right value
                        if (compareToString.toLowerCase().equals("true")) //right value is true boolean
                            rightVal = new LangTemplateValue(true);
                        else if (compareToString.toLowerCase().equals("false")) //right value is false boolean
                            rightVal = new LangTemplateValue(false);
                        else if (Pattern.matches("^-{0,1}[0-9]+$", compareToString) == true) //right value is number
                            rightVal = new LangTemplateValue(Integer.parseInt(compareToString));
                        else
                            rightVal = new LangTemplateValue(compareToString);

                        //know if comparison is true or false
                        if (compareToMode.equals("=")) {// compare mode is =
                            comparison = leftVal.value.equals(rightVal.value);
                        } else if (compareToMode.startsWith("<") || compareToMode.startsWith(">")) { // compare mode is < or >, if either value isn't a number then return false

                            if (leftVal.valueType == 1 && rightVal.valueType == 1) {

                                if (compareToMode.equals("<"))
                                    comparison = leftVal.valueInt < rightVal.valueInt;
                                else if (compareToMode.equals("<="))
                                    comparison = leftVal.valueInt <= rightVal.valueInt;
                                else if (compareToMode.equals(">"))
                                    comparison = leftVal.valueInt > rightVal.valueInt;
                                else
                                    comparison = leftVal.valueInt >= rightVal.valueInt;

                            }

                        } else { // check if either boolean = true, number != 0, or string.length() > 0

                            if (leftVal.valueType == 0)
                                comparison = leftVal.value.length() > 0;
                            else if (leftVal.valueType == 1)
                                comparison = leftVal.valueInt != 0;
                            else if (leftVal.valueType == 2)
                                comparison = leftVal.valueBool;
                            
                        }

                        if (comparison)
                            ret+= compareTrueString;
                        else
                            ret+= compareFalseString;
                        
                    }

                    //ret += this.get(templateString).value;
                } else if (arr.get(i).equals("<") || arr.get(i).equals(">") || arr.get(i).equals("=")) { // compare to mode (<, >, or =)
                    if (arr.get(i).equals("=") && (compareToMode.equals("<") || compareToMode.equals(">"))) // if the current val is = and < or > has already been decalred, make it <= or >=
                        compareToMode+= arr.get(i);
                    else
                        compareToMode = arr.get(i);
                    compareTo = true;
                    compareTrue = false;
                    compareFalse = false;
                } else if (arr.get(i).equals("?")) { // if comparison was true
                    compareTo = false;
                    compareTrue = true;
                    compareFalse = false;
                } else if (arr.get(i).equals(":")) { // if comparison was false
                    compareTo = false;
                    compareTrue = false;
                    compareFalse = true;
                } else if (compareTo) { // comparing to
                    compareToString += arr.get(i);
                } else if (compareTrue) { // if comparison was true
                    compareTrueString += arr.get(i);
                } else if (compareFalse) { // if comparison was false
                    compareFalseString += arr.get(i);
                } else { // templateString
                    templateString += arr.get(i);
                }

            } else { //reading normal

                if (escape) { // escape character before

                    escape = false;

                    switch (arr.get(i)) {
                        case "n":
                            ret+= "\n";
                            break;
                        case "t":
                            ret+= "\t";
                            break;
                        case "r":
                            ret+= "\r";
                            break;
                        case "&":
                            ret+= "\\&";
                            break;
                        default:
                            ret+= arr.get(i);
                    }

                } else if (arr.get(i).equals("\\")) { // escape character
                    escape = true;
                } else if (arr.get(i).equals("{")) { // start
                    readName = true;
                    templateString = "";
                    compareToMode = "";
                    compareToString = "";
                    compareTrueString = "";
                    compareFalseString = "";
                } else { // other
                    ret += arr.get(i);
                }

            }

        }

        return ret;
    }

    /**
     * Static method to process the colors in a string
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param str     string to be processed
     * @param noColor whether or not color should be applied (true hides the color)
     */
    public static String escapeColors(String str, Boolean noColor) {
        List<String> arr = Arrays.asList(str.split(""));

        String ret = "";

        boolean escape = false;
        boolean color = false;

        for (int i = 0; i < arr.size(); i++) {

            if (escape) { // escape character before

                escape = false;

                switch (arr.get(i)) {
                    case "n":
                        ret += "\n";
                        break;
                    case "t":
                        ret += "\t";
                        break;
                    case "r":
                        ret += "\r";
                        break;
                    default:
                        ret += arr.get(i);
                }

            } else if (color) { // color character before

                color = false;

                if (!noColor)
                    switch (arr.get(i)) {
                        case "0":
                            ret += ChatColor.BLACK;
                            break;
                        case "1":
                            ret += ChatColor.DARK_BLUE;
                            break;
                        case "2":
                            ret += ChatColor.DARK_GREEN;
                            break;
                        case "3":
                            ret += ChatColor.DARK_AQUA;
                            break;
                        case "4":
                            ret += ChatColor.DARK_RED;
                            break;
                        case "5":
                            ret += ChatColor.DARK_PURPLE;
                            break;
                        case "6":
                            ret += ChatColor.GOLD;
                            break;
                        case "7":
                            ret += ChatColor.GRAY;
                            break;
                        case "8":
                            ret += ChatColor.DARK_GRAY;
                            break;
                        case "9":
                            ret += ChatColor.BLUE;
                            break;
                        case "a":
                            ret += ChatColor.GREEN;
                            break;
                        case "b":
                            ret += ChatColor.AQUA;
                            break;
                        case "c":
                            ret += ChatColor.RED;
                            break;
                        case "d":
                            ret += ChatColor.LIGHT_PURPLE;
                            break;
                        case "e":
                            ret += ChatColor.YELLOW;
                            break;
                        case "f":
                            ret += ChatColor.WHITE;
                            break;

                        case "k":
                            ret += ChatColor.MAGIC;
                            break;
                        case "l":
                            ret += ChatColor.BOLD;
                            break;
                        case "m":
                            ret += ChatColor.STRIKETHROUGH;
                            break;
                        case "n":
                            ret += ChatColor.UNDERLINE;
                            break;
                        case "o":
                            ret += ChatColor.ITALIC;
                            break;
                        case "r":
                            ret += ChatColor.RESET;
                            break;
                    }

            } else if (arr.get(i).equals("\\")) { // escape character
                escape = true;
            } else if (arr.get(i).equals("&")) { // start color
                color = true;
            } else { // other
                ret += arr.get(i);
            }

        }

        return ret;
    }

    /**
     * Static method to process the colors in a string
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param str string to be processed
     */
    public static String escapeColors(String str) {
        return escapeColors(str, false);
    }

    /**
     * Static method to add escape slashes in front of ampersands
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param str string to be processed
     */
    public static String doubleEscapeColor(String str) {
        
        List<String> arr = Arrays.asList(str.split(""));

        String newStr = "";

        for (String ch : arr) {
            if (ch.equals("&"))
                newStr+="\\&";
            else {
                newStr+= ch;
            }
        }

        return newStr;

    }

    /**
     * Static method to add escape slashes in front of slashes
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param str string to be processed
     */
    public static String doubleEscapeSlashes(String str) {
        
        List<String> arr = Arrays.asList(str.split(""));

        String newStr = "";

        for (String ch : arr) {
            if (ch.equals("\\"))
                newStr+="\\\\";
            else {
                newStr+= ch;
            }
        }

        return newStr;

    }

    /**
     * Static method to add escape slashes in front of pipe characters
     *
     * @author Ashley Zomo
     * @version 1.0.0
     * @since 2020-12-03
     * @param str string to be processed
     */
    public static String doubleEscapePipe(String str) {
        
        List<String> arr = Arrays.asList(str.split(""));

        String newStr = "";

        for (String ch : arr) {
            if (ch.equals("\\|"))
                newStr+="\\|";
            else {
                newStr+= ch;
            }
        }

        return newStr;

    }

    /**
     * Static method to add escape slashes in front of everything necessary
     *
     * @author Ashley Zomo
     * @version 1.0.1
     * @since 2020-12-17
     * @param str string to be processed
     */
    public static String doubleEscapeAll(String str) {
        List<String> arr = Arrays.asList(str.split(""));

        String newStr = "";

        for (String ch : arr) {
            if (ch.equals("&"))
                newStr += "\\&";
            else if (ch.equals("\\"))
                newStr += "\\\\";
            else if (ch.equals("\\|"))
                newStr += "\\|";
            else
                newStr += ch;
        }

        return newStr;
    }

}