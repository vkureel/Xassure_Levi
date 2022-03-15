package com.xassure.dbTables;

import javax.persistence.*;

@Entity
@Table(name = "orderdetails")
public class orderdetails {

    private int quantityOrdered;
    private int orderLineNumber;
    private String priceEach;
    private orderdetailsId _orderdetailsId;
    private orders _orders;
    private products _products;

    public orderdetails() {
    }

    @Column(name = "quantityOrdered", nullable = false, length = 11)
    public int getquantityOrdered() {
        return this.quantityOrdered;
    }

    public void setquantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    @Column(name = "orderLineNumber", nullable = false, length = 6)
    public int getorderLineNumber() {
        return this.orderLineNumber;
    }

    public void setorderLineNumber(int orderLineNumber) {
        this.orderLineNumber = orderLineNumber;
    }

    @Column(name = "priceEach", nullable = false, length = 10)
    public String getpriceEach() {
        return this.priceEach;
    }

    public void setpriceEach(String priceEach) {
        this.priceEach = priceEach;
    }

    @EmbeddedId
    public orderdetailsId getorderdetailsId() {
        return this._orderdetailsId;
    }

    public void setorderdetailsId(orderdetailsId _orderdetailsId) {
        this._orderdetailsId = _orderdetailsId;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "orderNumber", insertable = false, updatable = false)
    public orders getorders() {
        return this._orders;
    }

    public void setorders(orders _orders) {
        this._orders = _orders;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "productCode", insertable = false, updatable = false)
    public products getproducts() {
        return this._products;
    }

    public void setproducts(products _products) {
        this._products = _products;
    }

}
