package com.example.jimmer.dto;

import java.math.BigDecimal;

/**
 * A plain JavaBean with no dependency on Jimmer or Lombok.
 */
public class BookDto {

    private long id;
    private String title;
    private BigDecimal price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
