package com.bookmyroute.dto.response;

import java.math.BigDecimal;

public class PaymentOrderResponse {

    private String orderId;          // Razorpay order id  e.g. order_xyz123
    private BigDecimal amount;       // Amount in INR (NOT paise)
    private String currency;
    private String keyId;            // Razorpay public key – safe to expose
    private String companyName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String description;

    public PaymentOrderResponse() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String orderId;
        private BigDecimal amount;
        private String currency;
        private String keyId;
        private String companyName;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String description;

        public Builder orderId(String v)       { this.orderId = v; return this; }
        public Builder amount(BigDecimal v)     { this.amount = v; return this; }
        public Builder currency(String v)       { this.currency = v; return this; }
        public Builder keyId(String v)          { this.keyId = v; return this; }
        public Builder companyName(String v)    { this.companyName = v; return this; }
        public Builder customerName(String v)   { this.customerName = v; return this; }
        public Builder customerEmail(String v)  { this.customerEmail = v; return this; }
        public Builder customerPhone(String v)  { this.customerPhone = v; return this; }
        public Builder description(String v)    { this.description = v; return this; }

        public PaymentOrderResponse build() {
            PaymentOrderResponse r = new PaymentOrderResponse();
            r.orderId = orderId; r.amount = amount; r.currency = currency;
            r.keyId = keyId; r.companyName = companyName;
            r.customerName = customerName; r.customerEmail = customerEmail;
            r.customerPhone = customerPhone; r.description = description;
            return r;
        }
    }

    public String getOrderId()        { return orderId; }
    public BigDecimal getAmount()     { return amount; }
    public String getCurrency()       { return currency; }
    public String getKeyId()          { return keyId; }
    public String getCompanyName()    { return companyName; }
    public String getCustomerName()   { return customerName; }
    public String getCustomerEmail()  { return customerEmail; }
    public String getCustomerPhone()  { return customerPhone; }
    public String getDescription()    { return description; }
}