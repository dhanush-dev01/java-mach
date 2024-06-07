package org.mach.source.model.stripe;

import java.util.ArrayList;

public class Object {
    public String id;
    public String object;
    public Long amount;
    public int amount_capturable;
    public AmountDetails amount_details;
    public int amount_received;
    public Object application;
    public Object application_fee_amount;
    public Object automatic_payment_methods;
    public Object canceled_at;
    public Object cancellation_reason;
    public String capture_method;
    public String client_secret;
    public String confirmation_method;
    public int created;
    public String currency;
    public Object customer;
    public Object description;
    public Object invoice;
    public Object last_payment_error;
    public String latest_charge;
    public boolean livemode;
    public Metadata metadata;
    public Object next_action;
    public Object on_behalf_of;
    public String payment_method;
    public Object payment_method_configuration_details;
    public PaymentMethodOptions payment_method_options;
    public ArrayList<String> payment_method_types;
    public Object processing;
    public Object receipt_email;
    public Object review;
    public Object setup_future_usage;
    public Shipping shipping;
    public Object source;
    public Object statement_descriptor;
    public Object statement_descriptor_suffix;
    public String status;
    public Object transfer_data;
    public Object transfer_group;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public int getAmount_capturable() {
        return amount_capturable;
    }

    public void setAmount_capturable(int amount_capturable) {
        this.amount_capturable = amount_capturable;
    }

    public AmountDetails getAmount_details() {
        return amount_details;
    }

    public void setAmount_details(AmountDetails amount_details) {
        this.amount_details = amount_details;
    }

    public int getAmount_received() {
        return amount_received;
    }

    public void setAmount_received(int amount_received) {
        this.amount_received = amount_received;
    }

    public Object getApplication() {
        return application;
    }

    public void setApplication(Object application) {
        this.application = application;
    }

    public Object getApplication_fee_amount() {
        return application_fee_amount;
    }

    public void setApplication_fee_amount(Object application_fee_amount) {
        this.application_fee_amount = application_fee_amount;
    }

    public Object getAutomatic_payment_methods() {
        return automatic_payment_methods;
    }

    public void setAutomatic_payment_methods(Object automatic_payment_methods) {
        this.automatic_payment_methods = automatic_payment_methods;
    }

    public Object getCanceled_at() {
        return canceled_at;
    }

    public void setCanceled_at(Object canceled_at) {
        this.canceled_at = canceled_at;
    }

    public Object getCancellation_reason() {
        return cancellation_reason;
    }

    public void setCancellation_reason(Object cancellation_reason) {
        this.cancellation_reason = cancellation_reason;
    }

    public String getCapture_method() {
        return capture_method;
    }

    public void setCapture_method(String capture_method) {
        this.capture_method = capture_method;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getConfirmation_method() {
        return confirmation_method;
    }

    public void setConfirmation_method(String confirmation_method) {
        this.confirmation_method = confirmation_method;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Object getCustomer() {
        return customer;
    }

    public void setCustomer(Object customer) {
        this.customer = customer;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public Object getInvoice() {
        return invoice;
    }

    public void setInvoice(Object invoice) {
        this.invoice = invoice;
    }

    public Object getLast_payment_error() {
        return last_payment_error;
    }

    public void setLast_payment_error(Object last_payment_error) {
        this.last_payment_error = last_payment_error;
    }

    public String getLatest_charge() {
        return latest_charge;
    }

    public void setLatest_charge(String latest_charge) {
        this.latest_charge = latest_charge;
    }

    public boolean isLivemode() {
        return livemode;
    }

    public void setLivemode(boolean livemode) {
        this.livemode = livemode;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Object getNext_action() {
        return next_action;
    }

    public void setNext_action(Object next_action) {
        this.next_action = next_action;
    }

    public Object getOn_behalf_of() {
        return on_behalf_of;
    }

    public void setOn_behalf_of(Object on_behalf_of) {
        this.on_behalf_of = on_behalf_of;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public Object getPayment_method_configuration_details() {
        return payment_method_configuration_details;
    }

    public void setPayment_method_configuration_details(Object payment_method_configuration_details) {
        this.payment_method_configuration_details = payment_method_configuration_details;
    }

    public PaymentMethodOptions getPayment_method_options() {
        return payment_method_options;
    }

    public void setPayment_method_options(PaymentMethodOptions payment_method_options) {
        this.payment_method_options = payment_method_options;
    }

    public ArrayList<String> getPayment_method_types() {
        return payment_method_types;
    }

    public void setPayment_method_types(ArrayList<String> payment_method_types) {
        this.payment_method_types = payment_method_types;
    }

    public Object getProcessing() {
        return processing;
    }

    public void setProcessing(Object processing) {
        this.processing = processing;
    }

    public Object getReceipt_email() {
        return receipt_email;
    }

    public void setReceipt_email(Object receipt_email) {
        this.receipt_email = receipt_email;
    }

    public Object getReview() {
        return review;
    }

    public void setReview(Object review) {
        this.review = review;
    }

    public Object getSetup_future_usage() {
        return setup_future_usage;
    }

    public void setSetup_future_usage(Object setup_future_usage) {
        this.setup_future_usage = setup_future_usage;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getStatement_descriptor() {
        return statement_descriptor;
    }

    public void setStatement_descriptor(Object statement_descriptor) {
        this.statement_descriptor = statement_descriptor;
    }

    public Object getStatement_descriptor_suffix() {
        return statement_descriptor_suffix;
    }

    public void setStatement_descriptor_suffix(Object statement_descriptor_suffix) {
        this.statement_descriptor_suffix = statement_descriptor_suffix;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getTransfer_data() {
        return transfer_data;
    }

    public void setTransfer_data(Object transfer_data) {
        this.transfer_data = transfer_data;
    }

    public Object getTransfer_group() {
        return transfer_group;
    }

    public void setTransfer_group(Object transfer_group) {
        this.transfer_group = transfer_group;
    }
}
