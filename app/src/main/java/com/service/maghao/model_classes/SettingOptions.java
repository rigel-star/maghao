package com.service.maghao.model_classes;

public class SettingOptions {

    private String optionName;
    private int optionIcon;

    public SettingOptions(){}

    public SettingOptions(String optionName, int optionIcon){
        this.optionName = optionName;
        this.optionIcon = optionIcon;}

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public int getOptionIcon() {
        return optionIcon;
    }

    public void setOptionIcon(int optionIcon) {
        this.optionIcon = optionIcon;
    }
}
