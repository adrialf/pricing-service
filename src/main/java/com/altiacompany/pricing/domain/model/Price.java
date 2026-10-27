package com.altiacompany.pricing.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain entity representing a product price configuration.
 */
public class Price {

    private final Long brandId;
    private final Instant startDate;
    private final Instant endDate;
    private final Long priceList;
    private final Long productId;
    private final Integer priority;
    private final BigDecimal amount;
    private final String currency;

    public Price(Long brandId, Instant startDate, Instant endDate,
            Long priceList, Long productId, Integer priority, BigDecimal amount,
            String currency) {
        this.brandId = brandId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.priceList = priceList;
        this.productId = productId;
        this.priority = priority;
        this.amount = amount;
        this.currency = currency;
    }

    public Long getBrandId() {
        return brandId;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Long getPriceList() {
        return priceList;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getPriority() {
        return priority;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Price price1 = (Price) o;
        return Objects.equals(brandId, price1.brandId)
                && Objects.equals(startDate, price1.startDate)
                && Objects.equals(endDate, price1.endDate)
                && Objects.equals(priceList, price1.priceList)
                && Objects.equals(productId, price1.productId)
                && Objects.equals(priority, price1.priority)
                && Objects.equals(amount, price1.amount)
                && Objects.equals(currency, price1.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brandId, startDate, endDate, priceList, productId,
                priority, amount, currency);
    }

    @Override
    public String toString() {
        // @formatter:off
        return "Price{" +
                "brandId=" + brandId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", priceList=" + priceList +
                ", productId=" + productId +
                ", priority=" + priority +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                '}';
        // @formatter:on
    }
}
