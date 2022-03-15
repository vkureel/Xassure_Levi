package com.xassure.dbTables;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offices")
public class offices {

    private String country;
    private String city;
    private String phone;
    private String postalCode;
    private String officeCode;
    private String addressLine1;
    private String addressLine2;
    private String state;
    private String territory;
    private Set<employees> _employees = new HashSet<employees>(0);

    public offices() {
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

    @Column(name = "phone", nullable = false, length = 50)
    public String getphone() {
        return this.phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    @Column(name = "postalCode", nullable = false, length = 15)
    public String getpostalCode() {
        return this.postalCode;
    }

    public void setpostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Column(name = "officeCode", nullable = false, length = 10)
    @Id
    @GeneratedValue
    public String getofficeCode() {
        return this.officeCode;
    }

    public void setofficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    @Column(name = "addressLine1", nullable = false, length = 50)
    public String getaddressLine1() {
        return this.addressLine1;
    }

    public void setaddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
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

    @Column(name = "territory", nullable = false, length = 10)
    public String getterritory() {
        return this.territory;
    }

    public void setterritory(String territory) {
        this.territory = territory;
    }

    @OneToMany(mappedBy = "offices", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<employees> getemployees() {
        return this._employees;
    }

    public void setemployees(Set<employees> _employees) {
        this._employees = _employees;
    }

}
