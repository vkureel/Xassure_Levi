package com.xassure.framework.driver;

public class PageInfo<X> {

    private X pageVariable;

    public X getPageVariable() {
        return pageVariable;
    }

    public void setPageVariable(X pageVariable) {
        this.pageVariable = pageVariable;
    }

    public PageInfo(X pageVariable) {

        this.pageVariable = pageVariable;
    }

}
