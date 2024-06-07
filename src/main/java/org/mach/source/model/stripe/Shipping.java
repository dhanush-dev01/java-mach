package org.mach.source.model.stripe;

public class Shipping {
        public Address address;
        public Object carrier;
        public String name;
        public Object phone;
        public Object tracking_number;

        public Address getAddress() {
                return address;
        }

        public void setAddress(Address address) {
                this.address = address;
        }

        public Object getCarrier() {
                return carrier;
        }

        public void setCarrier(Object carrier) {
                this.carrier = carrier;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Object getPhone() {
                return phone;
        }

        public void setPhone(Object phone) {
                this.phone = phone;
        }

        public Object getTracking_number() {
                return tracking_number;
        }

        public void setTracking_number(Object tracking_number) {
                this.tracking_number = tracking_number;
        }
}
