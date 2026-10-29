package com.altiacompany.pricing.application.port.input;

import java.util.Objects;

/**
 * Immutable query object containing filter criteria for retrieving products.
 */
public class GetProductsQuery {

    private final String q;

    private GetProductsQuery(Builder builder) {
        this.q = builder.q;
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

        GetProductsQuery that = (GetProductsQuery) o;
        return Objects.equals(q, that.q);
    }

    @Override
    public int hashCode() {
        return Objects.hash(q);
    }

    @Override
    public String toString() {
        // @formatter:off
        return "GetProductsQuery{" +
                "q='" + q + '\'' +
                '}';
        // @formatter:on
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String q;

        public Builder q(String q) {
            this.q = q;
            return this;
        }

        public GetProductsQuery build() {
            return new GetProductsQuery(this);
        }
    }
}
