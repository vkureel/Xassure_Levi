package com.xassure.reporting.testcasesummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Module {

    private String moduleName;
    private List<LocaleLanguage> localeLanguageList;

    public Module(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String toString() {
        return "Module{" +
                "moduleName='" + moduleName + '\'' +
                ", localeLanguageList=" + localeLanguageList +
                '}';
    }

    public String getModuleName() {
        return moduleName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Module)) return false;
        Module module = (Module) o;
        return getModuleName().equals(module.getModuleName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModuleName());
    }

    public List<LocaleLanguage> getLocaleLanguageList() {
        if (localeLanguageList == null) {
            localeLanguageList = new ArrayList<>();
        }
        return localeLanguageList;
    }

}
