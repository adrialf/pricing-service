package com.altiacompany.pricing.application.port.input;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable query parameter object containing search filters for pricing
 * queries.
 */
public class GetPriceQuery {

    private final Instant applicationDate;
    private final Long productId;
    private final Long brandId;

    private GetPriceQuery(Builder builder) {
        this.applicationDate = builder.applicationDate;
        this.productId = builder.productId;
        this.brandId = builder.brandId;
    }

    public Instant getApplicationDate() {
        return applicationDate;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getBrandId() {
        return brandId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GetPriceQuery that = (GetPriceQuery) o;
        return Objects.equals(applicationDate, that.applicationDate)
                && Objects.equals(productId, that.productId)
                && Objects.equals(brandId, that.brandId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationDate, productId, brandId);
    }

    @Override
    public String toString() {
        // @formatter:off
        return "GetPriceQuery{" +
                "applicationDate=" + applicationDate +
                ", productId=" + productId +
                ", brandId=" + brandId +
                '}';
        // @formatter:on
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Instant applicationDate;
        private Long productId;
        private Long brandId;

        public Builder applicationDate(Instant applicationDate) {
            this.applicationDate = applicationDate;
            return this;
        }

        public Builder productId(Long productId) {
            this.productId = productId;
            return this;
        }

        public Builder brandId(Long brandId) {
            this.brandId = brandId;
            return this;
        }

        public GetPriceQuery build() {
            Objects.requireNonNull(applicationDate,
                    "applicationDate cannot be null");
            Objects.requireNonNull(productId, "productId cannot be null");
            Objects.requireNonNull(brandId, "brandId cannot be null");
            return new GetPriceQuery(this);
        }
    }

}
