package org.mach.source.dto;

import com.commercetools.api.models.common.TypedMoney;

public class ProductCreateDTO {
    private String name;
    private TypedMoney price;
    private String imageUrl;
    private String description;
    private String categorykey;
    private String slug;
    private String producttypekey;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategorykey() {
        return categorykey;
    }

    public void setCategorykey(String categorykey) {
        this.categorykey = categorykey;
    }

    public String getProducttypekey() {
        return producttypekey;
    }

    public void setProducttypekey(String producttypekey) {
        this.producttypekey = producttypekey;
    }
}
