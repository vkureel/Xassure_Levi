package com.xassure.dbTables;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productlines")
public class productlines {

    private String productLine;
    private String image;
    private String textDescription;
    private String htmlDescription;
    private Set<products> _products = new HashSet<products>(0);

    public productlines() {
    }

    @Column(name = "productLine", nullable = false, length = 50)
    @Id
    @GeneratedValue
    public String getproductLine() {
        return this.productLine;
    }

    public void setproductLine(String productLine) {
        this.productLine = productLine;
    }

    @Column(name = "image", nullable = true, length = 16777215)
    public String getimage() {
        return this.image;
    }

    public void setimage(String image) {
        this.image = image;
    }

    @Column(name = "textDescription", nullable = true, length = 4000)
    public String gettextDescription() {
        return this.textDescription;
    }

    public void settextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    @Column(name = "htmlDescription", nullable = true, length = 16777215)
    public String gethtmlDescription() {
        return this.htmlDescription;
    }

    public void sethtmlDescription(String htmlDescription) {
        this.htmlDescription = htmlDescription;
    }

    @OneToMany(mappedBy = "productlines", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Set<products> getproducts() {
        return this._products;
    }

    public void setproducts(Set<products> _products) {
        this._products = _products;
    }

}
