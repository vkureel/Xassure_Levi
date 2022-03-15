package com.xassure.dbTables;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
public class orders {

    private int orderNumber;
    private String comments;
    private Date requiredDate;
    private Date orderDate;
    private Date shippedDate;
    private String status;
    private Set<orderdetails> _orderdetails = new HashSet<orderdetails>(0);
    private customers _customers;

    public orders() {
    }

    @Column(name = "orderNumber", nullable = false, length = 11)
    @Id
    @GeneratedValue
    public int getorderNumber() {
        return this.orderNumber;
    }

    public void setorderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Column(name = "comments", nullable = true, length = 65535)
    public String getcomments() {
        return this.comments;
    }

    public void setcomments(String comments) {
        this.comments = comments;
    }

    @Column(name = "requiredDate", nullable = false, length = 10)
    public Date getrequiredDate() {
        return this.requiredDate;
    }

    public void setrequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }

    @Column(name = "orderDate", nullable = false, length = 10)
    public Date getorderDate() {
        return this.orderDate;
    }

    public void setorderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Column(name = "shippedDate", nullable = true, length = 10)
    public Date getshippedDate() {
        return this.shippedDate;
    }

    public void setshippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    @Column(name = "status", nullable = false, length = 15)
    public String getstatus() {
        return this.status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "orders", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<orderdetails> getorderdetails() {
        return this._orderdetails;
    }

    public void setorderdetails(Set<orderdetails> _orderdetails) {
        this._orderdetails = _orderdetails;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customerNumber", insertable = false, updatable = false)
    public customers getcustomers() {
        return this._customers;
    }

    public void setcustomers(customers _customers) {
        this._customers = _customers;
    }

}
