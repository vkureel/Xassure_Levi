package com.xassure.dbTables;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class users {

    private String zipcode;
    private String website;
    private String phone;
    private String address2;
    private String address1;
    private String name;
    private String company;
    private String City;
    private String email;
    private usersId _usersId;

    public users() {
    }

    @Column(name = "zipcode", nullable = true, length = 45)
    public String getzipcode() {
        return this.zipcode;
    }

    public void setzipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Column(name = "website", nullable = true, length = 45)
    public String getwebsite() {
        return this.website;
    }

    public void setwebsite(String website) {
        this.website = website;
    }

    @Column(name = "phone", nullable = true, length = 45)
    public String getphone() {
        return this.phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }

    @Column(name = "address2", nullable = true, length = 45)
    public String getaddress2() {
        return this.address2;
    }

    public void setaddress2(String address2) {
        this.address2 = address2;
    }

    @Column(name = "address1", nullable = true, length = 45)
    public String getaddress1() {
        return this.address1;
    }

    public void setaddress1(String address1) {
        this.address1 = address1;
    }

    @Column(name = "name", nullable = true, length = 45)
    public String getname() {
        return this.name;
    }

    public void setname(String name) {
        this.name = name;
    }

    @Column(name = "company", nullable = true, length = 45)
    public String getcompany() {
        return this.company;
    }

    public void setcompany(String company) {
        this.company = company;
    }

    @Column(name = "City", nullable = true, length = 45)
    public String getCity() {
        return this.City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    @Column(name = "email", nullable = true, length = 45)
    public String getemail() {
        return this.email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    @EmbeddedId
    public usersId getusersId() {
        return this._usersId;
    }

    public void setusersId(usersId _usersId) {
        this._usersId = _usersId;
    }

}
