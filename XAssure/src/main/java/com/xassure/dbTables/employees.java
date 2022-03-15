package com.xassure.dbTables;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees")
public class employees {

    private String lastName;
    private String firstName;
    private String extension;
    private String jobTitle;
    private employees supreportsTo;
    private Set<employees> subreportsTo = new HashSet<employees>(0);
    private String email;
    private int employeeNumber;
    private offices _offices;
    private Set<customers> _customers = new HashSet<customers>(0);

    public employees() {
    }

    @Column(name = "lastName", nullable = false, length = 50)
    public String getlastName() {
        return this.lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "firstName", nullable = false, length = 50)
    public String getfirstName() {
        return this.firstName;
    }

    public void setfirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "extension", nullable = false, length = 10)
    public String getextension() {
        return this.extension;
    }

    public void setextension(String extension) {
        this.extension = extension;
    }

    @Column(name = "jobTitle", nullable = false, length = 50)
    public String getjobTitle() {
        return this.jobTitle;
    }

    public void setjobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "reportsTo")
    public employees getsupreportsTo() {
        return this.supreportsTo;
    }

    @OneToMany(mappedBy = "supreportsTo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<employees> getsubreportsTo() {
        return this.subreportsTo;
    }

    public void setsupreportsTo(employees supreportsTo) {
        this.supreportsTo = supreportsTo;
    }

    public void setsubreportsTo(Set<employees> subreportsTo) {
        this.subreportsTo = subreportsTo;
    }

    @Column(name = "email", nullable = false, length = 100)
    public String getemail() {
        return this.email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    @Column(name = "employeeNumber", nullable = false, length = 11)
    @Id
    @GeneratedValue
    public int getemployeeNumber() {
        return this.employeeNumber;
    }

    public void setemployeeNumber(int employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "officeCode", insertable = false, updatable = false)
    public offices getoffices() {
        return this._offices;
    }

    public void setoffices(offices _offices) {
        this._offices = _offices;
    }

    @OneToMany(mappedBy = "employees", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<customers> getcustomers() {
        return this._customers;
    }

    public void setcustomers(Set<customers> _customers) {
        this._customers = _customers;
    }

}
