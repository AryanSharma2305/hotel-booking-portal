import { CurrencyPipe, DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AlternativeStay, Availability, HotelDetails } from '../../models';
import { ApiService, addDays, errorMessage, fieldErrors, todayIso } from '../../services/api.service';

@Component({
  selector: 'app-hotel-detail',
  imports: [FormsModule, RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './hotel-detail.component.html',
  styleUrl: './hotel-detail.component.css',
})
export class HotelDetailComponent implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  today = todayIso();
  hotelId = 0;
  hotel: HotelDetails | null = null;
  pageError = '';

  checkIn = '';
  checkOut = '';
  availability: Availability | null = null;
  checking = false;
  availabilityError = '';

  guestName = '';
  guestEmail = '';
  booking = false;
  bookingError = '';
  errors: Record<string, string> = {};

  ngOnInit(): void {
    this.hotelId = Number(this.route.snapshot.paramMap.get('id'));
    this.checkIn = this.route.snapshot.queryParamMap.get('checkIn') ?? '';
    this.checkOut = this.route.snapshot.queryParamMap.get('checkOut') ?? '';

    this.api.getHotel(this.hotelId).subscribe({
      next: (hotel) => {
        this.hotel = hotel;
        if (this.checkIn && this.checkOut) {
          this.checkAvailability();
        }
      },
      error: (err) => (this.pageError = errorMessage(err)),
    });
  }

  onCheckInChange(): void {
    if (this.checkIn && (!this.checkOut || this.checkOut <= this.checkIn)) {
      this.checkOut = addDays(this.checkIn, 1);
    }
    this.onDatesChanged();
  }

  /** Any date change hides the old price, so the user never books with a stale total. */
  onDatesChanged(): void {
    this.availability = null;
    this.availabilityError = '';
    this.bookingError = '';
  }

  checkAvailability(): void {
    this.onDatesChanged();
    if (!this.checkIn || !this.checkOut) {
      this.availabilityError = 'Please select both check-in and check-out dates.';
      return;
    }
    if (this.checkOut <= this.checkIn) {
      this.availabilityError = 'Check-out date must be after the check-in date.';
      return;
    }

    this.checking = true;
    // keep the dates in the URL so a refresh shows the same result
    this.router.navigate([], {
      queryParams: { checkIn: this.checkIn, checkOut: this.checkOut },
      replaceUrl: true,
    });
    this.api.checkAvailability(this.hotelId, this.checkIn, this.checkOut).subscribe({
      next: (result) => {
        this.availability = result;
        this.checking = false;
      },
      error: (err) => {
        this.availabilityError = errorMessage(err);
        this.checking = false;
      },
    });
  }

  useAlternative(stay: AlternativeStay): void {
    this.checkIn = stay.checkIn;
    this.checkOut = stay.checkOut;
    this.checkAvailability();
  }

  confirmBooking(): void {
    this.errors = {};
    this.bookingError = '';
    if (!this.guestName.trim()) this.errors['guestName'] = 'Name is required';
    if (!this.guestEmail.trim()) this.errors['guestEmail'] = 'Email is required';
    if (Object.keys(this.errors).length > 0) return;

    this.booking = true;
    this.api
      .createBooking({
        hotelId: this.hotelId,
        guestName: this.guestName,
        guestEmail: this.guestEmail,
        checkIn: this.checkIn,
        checkOut: this.checkOut,
      })
      .subscribe({
        next: (booking) => this.router.navigate(['/confirmation'], { state: { booking } }),
        error: (err: HttpErrorResponse) => {
          this.booking = false;
          this.bookingError = errorMessage(err);
          this.errors = fieldErrors(err);
          if (err.status === 409) {
            // someone else took the last room meanwhile: refresh availability (and date suggestions)
            const message = this.bookingError;
            this.checkAvailability();
            this.bookingError = message;
          }
        },
      });
  }
}
