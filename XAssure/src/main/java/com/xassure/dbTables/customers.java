package com.xassure.dbTables;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customers")
public class customers {

    private String country;
    private String city;
    private String contactFirstName;
    private String postalCode;
    private int customerNumber;
    private String customerName;
    private String phone;
    private String addressLine1;
    private String creditLimit;
    private String contactLastName;
    private String addressLine2;
    private String state;
    private Set<payments> _payments = new HashSet<payments>(0);
    private Set<orders> _orders = new HashSet<orders>(0);
    private employees _employees;

    public customers() {
    }

    @Column(name = "country", nullable = false, length = 50)
    public String getcountry() {
        return this.country;
    }

    public void setcountry(String country) {
        this.country = country;
    }

    @Column(name = "city", nullable = false, length = 50)
    public String getcity() {
        return this.city;
    }

    public void setcity(String city) {
        this.city = city;
    }

    @Column(name = "contactFirstName", nullable = false, length = 50)
    public String getcontactFirstName() {
        return this.contactFirstName;
    }

    public void setcontactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    @Column(name = "postalCode", nullable = true, length = 15)
    public String getpostalCode() {
        return this.postalCode;
    }

    public void setpostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Column(name = "customerNumber", nullable = false, length = 11)
    @Id
    @GeneratedValue
    public int getcustomerNumber() {
        return this.customerNumber;
    }

    public void setcustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Column(name = "customerName", nullable = false, length = 50)
    public String getcustomerName() {
        return this.customerName;
    }

    public void setcustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Column(name = "phone", nullable = false, length = 50)
    public String getphone() {
        return this.phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    @Column(name = "addressLine1", nullable = false, length = 50)
    public String getaddressLine1() {
        return this.addressLine1;
    }

    public void setaddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @Column(name = "creditLimit", nullable = true, length = 10)
    public String getcreditLimit() {
        return this.creditLimit;
    }

    public void setcreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Column(name = "contactLastName", nullable = false, length = 50)
    public String getcontactLastName() {
        return this.contactLastName;
    }

    public void setcontactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    @Column(name = "addressLine2", nullable = true, length = 50)
    public String getaddressLine2() {
        return this.addressLine2;
    }

    public void setaddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Column(name = "state", nullable = true, length = 50)
    public String getstate() {
        return this.state;
    }

    public void setstate(String state) {
        this.state = state;
    }

    @OneToMany(mappedBy = "customers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<payments> getpayments() {
        return this._payments;
    }

    public void setpayments(Set<payments> _payments) {
        this._payments = _payments;
    }

    @OneToMany(mappedBy = "customers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<orders> getorders() {
        return this._orders;
    }

    public void setorders(Set<orders> _orders) {
        this._orders = _orders;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "salesRepEmployeeNumber", insertable = false, updatable = false)
    public employees getemployees() {
        return this._employees;
    }

    public void setemployees(employees _employees) {
        this._employees = _employees;
    }

}
