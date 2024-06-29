package org.mach.source.dto;

import com.commercetools.api.models.common.TypedMoney;

public class ProductDTO {
    private String name;
    private TypedMoney price;
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypedMoney getPrice() {
        return price;
    }

    public void setPrice(TypedMoney price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
