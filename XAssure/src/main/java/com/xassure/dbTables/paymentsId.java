package com.xassure.dbTables;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class paymentsId
        implements Serializable {

    private int customerNumber;
    private String checkNumber;

    public paymentsId() {
    }

    public paymentsId(int customerNumber, String checkNumber) {
        this.customerNumber = customerNumber;
        this.checkNumber = checkNumber;
    }

    @Column(name = "customerNumber", nullable = false, length = 11)
    public int getcustomerNumber() {
        return this.customerNumber;
    }

    public void setcustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Column(name = "checkNumber", nullable = false, length = 50)
    public String getcheckNumber() {
        return this.checkNumber;
    }

    public void setcheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        paymentsId obj2 = ((paymentsId) obj);
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        return (this.customerNumber == obj2.getcustomerNumber()) && this.checkNumber.equals(obj2.getcheckNumber());
    }

    public int hashcode() {
        int tmp = 0;
        tmp = (customerNumber + checkNumber).hashCode();
        return tmp;
    }

}
