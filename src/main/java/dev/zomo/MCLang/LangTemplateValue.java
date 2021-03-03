package dev.zomo.MCLang;

public class LangTemplateValue {

    public String value = "";
    public int valueInt = 0;
    public Boolean valueBool = false;
    public int valueType = -1;

    LangTemplateValue() {

    }
    LangTemplateValue(String val) {
        value = val;
        valueType = 0;
    }
    LangTemplateValue(int val) {
        value = String.valueOf(val);
        valueInt = val;
        valueType = 1;
    }
    LangTemplateValue(Boolean val) {
        value = String.valueOf(val);
        valueBool = val;
        valueType = 2;
    }
}