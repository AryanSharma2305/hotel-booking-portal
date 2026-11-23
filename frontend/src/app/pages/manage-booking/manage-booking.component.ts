import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Booking } from '../../models';
import { ApiService, errorMessage, todayIso } from '../../services/api.service';

@Component({
  selector: 'app-manage-booking',
  imports: [FormsModule, CurrencyPipe, DatePipe],
  templateUrl: './manage-booking.component.html',
  styleUrl: './manage-booking.component.css',
})
export class ManageBookingComponent implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);

  reference = '';
  email = '';
  booking: Booking | null = null;
  loading = false;
  cancelling = false;
  error = '';
  message = '';

  ngOnInit(): void {
    this.reference = this.route.snapshot.queryParamMap.get('reference') ?? '';
  }

  findBooking(): void {
    this.error = '';
    this.message = '';
    this.booking = null;
    if (!this.reference.trim() || !this.email.trim()) {
      this.error = 'Please enter both the booking reference and the email used for booking.';
      return;
    }

    this.loading = true;
    this.api.getBooking(this.reference.trim(), this.email.trim()).subscribe({
      next: (booking) => {
        this.booking = booking;
        this.loading = false;
      },
      error: (err) => {
        this.error = errorMessage(err);
        this.loading = false;
      },
    });
  }

  get canCancel(): boolean {
    return !!this.booking && this.booking.status === 'CONFIRMED' && this.booking.checkIn >= todayIso();
  }

  cancelBooking(): void {
    if (!this.booking || !confirm('Cancel this booking? This cannot be undone.')) return;

    this.cancelling = true;
    this.error = '';
    this.api.cancelBooking(this.booking.bookingReference, this.email.trim()).subscribe({
      next: (booking) => {
        this.booking = booking;
        this.message = 'Your booking has been cancelled and the room has been released.';
        this.cancelling = false;
      },
      error: (err) => {
        this.error = errorMessage(err);
        this.cancelling = false;
      },
    });
  }
}
