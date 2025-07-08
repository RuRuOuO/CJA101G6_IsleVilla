package com.islevilla.patty.booking.model;

import com.islevilla.lai.members.model.Members;

public interface BookingEmailService {
    void sendBookingConfirmationEmail(Members member, Booking booking);
}