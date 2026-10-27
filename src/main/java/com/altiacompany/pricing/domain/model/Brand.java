package com.altiacompany.pricing.domain.model;

import java.util.Objects;

/**
 * Domain entity representing a brand.
 */
public class Brand {

    private final Long id;
    private final String name;
    private final String code;
    private final String description;
    private final Boolean active;

    public Brand(Long id, String name, String code, String description,
            Boolean active) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Brand brand = (Brand) o;
        return Objects.equals(id, brand.id)
                && Objects.equals(name, brand.name)
                && Objects.equals(code, brand.code)
                && Objects.equals(description, brand.description)
                && Objects.equals(active, brand.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, description, active);
    }

    @Override
    public String toString() {
        // @formatter:off
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
        // @formatter:on
    }
}
