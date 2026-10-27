package com.altiacompany.pricing.application.port.input;

import java.util.Objects;

/**
 * Immutable query object containing filter criteria for retrieving brands.
 */
public class GetBrandsQuery {

    private final Long id;
    private final Boolean active;
    private final String q;

    private GetBrandsQuery(Builder builder) {
        this.id = builder.id;
        this.active = builder.active;
        this.q = builder.q;
    }

    public Long getId() {
        return id;
    }

    public Boolean getActive() {
        return active;
    }

    public String getQ() {
        return q;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GetBrandsQuery that = (GetBrandsQuery) o;
        return Objects.equals(id, that.id)
                && Objects.equals(active, that.active)
                && Objects.equals(q, that.q);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, active, q);
    }

    @Override
    public String toString() {
        // @formatter:off
        return "GetBrandsQuery{" +
                "id=" + id +
                ", active=" + active +
                ", q='" + q + '\'' +
                '}';
        // @formatter:on
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Boolean active;
        private String q;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder q(String q) {
            this.q = q;
            return this;
        }

        public GetBrandsQuery build() {
            return new GetBrandsQuery(this);
        }
    }
}
