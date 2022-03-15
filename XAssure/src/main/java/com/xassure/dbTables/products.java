package com.xassure.dbTables;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class products {

    private int quantityInStock;
    private String buyPrice;
    private String MSRP;
    private String productCode;
    private String productScale;
    private String productName;
    private String productVendor;
    private String productDescription;
    private Set<orderdetails> _orderdetails = new HashSet<orderdetails>(0);
    private productlines _productlines;

    public products() {
    }

    @Column(name = "quantityInStock", nullable = false, length = 6)
    public int getquantityInStock() {
        return this.quantityInStock;
    }

    public void setquantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    @Column(name = "buyPrice", nullable = false, length = 10)
    public String getbuyPrice() {
        return this.buyPrice;
    }

    public void setbuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    @Column(name = "MSRP", nullable = false, length = 10)
    public String getMSRP() {
        return this.MSRP;
    }

    public void setMSRP(String MSRP) {
        this.MSRP = MSRP;
    }

    @Column(name = "productCode", nullable = false, length = 15)
    @Id
    @GeneratedValue
    public String getproductCode() {
        return this.productCode;
    }

    public void setproductCode(String productCode) {
        this.productCode = productCode;
    }

    @Column(name = "productScale", nullable = false, length = 10)
    public String getproductScale() {
        return this.productScale;
    }

    public void setproductScale(String productScale) {
        this.productScale = productScale;
    }

    @Column(name = "productName", nullable = false, length = 70)
    public String getproductName() {
        return this.productName;
    }

    public void setproductName(String productName) {
        this.productName = productName;
    }

    @Column(name = "productVendor", nullable = false, length = 50)
    public String getproductVendor() {
        return this.productVendor;
    }

    public void setproductVendor(String productVendor) {
        this.productVendor = productVendor;
    }

    @Column(name = "productDescription", nullable = false, length = 65535)
    public String getproductDescription() {
        return this.productDescription;
    }

    public void setproductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @OneToMany(mappedBy = "products", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<orderdetails> getorderdetails() {
        return this._orderdetails;
    }

    public void setorderdetails(Set<orderdetails> _orderdetails) {
        this._orderdetails = _orderdetails;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "productLine", insertable = false, updatable = false)
    public productlines getproductlines() {
        return this._productlines;
    }

    public void setproductlines(productlines _productlines) {
        this._productlines = _productlines;
    }

}
