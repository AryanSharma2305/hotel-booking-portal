import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Booking } from '../../models';

@Component({
  selector: 'app-confirmation',
  imports: [RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css',
})
export class ConfirmationComponent {
  // passed from the hotel page through router state (not in the URL, so the email isn't exposed)
  booking: Booking | null = history.state?.booking ?? null;
  copied = false;

  copyReference(): void {
    if (!this.booking) return;
    navigator.clipboard?.writeText(this.booking.bookingReference).then(() => (this.copied = true));
  }
}
