import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { HotelSummary } from '../../models';
import { ApiService, addDays, errorMessage, todayIso } from '../../services/api.service';

@Component({
  selector: 'app-search',
  imports: [FormsModule, RouterLink, CurrencyPipe],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css',
})
export class SearchComponent implements OnInit {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  today = todayIso();
  location = '';
  checkIn = '';
  checkOut = '';

  locations: string[] = [];
  hotels: HotelSummary[] = [];
  loading = false;
  error = '';

  ngOnInit(): void {
    this.api.getLocations().subscribe({ next: (list) => (this.locations = list) });

    // the search lives in the URL, so results survive a page refresh and the back button works
    this.route.queryParamMap.subscribe((params) => {
      this.location = params.get('location') ?? '';
      this.checkIn = params.get('checkIn') ?? '';
      this.checkOut = params.get('checkOut') ?? '';
      this.loadHotels();
    });
  }

  onCheckInChange(): void {
    if (this.checkIn && (!this.checkOut || this.checkOut <= this.checkIn)) {
      this.checkOut = addDays(this.checkIn, 1);
    }
  }

  search(): void {
    this.error = '';
    if (!!this.checkIn !== !!this.checkOut) {
      this.error = 'Please select both check-in and check-out dates, or leave both empty.';
      return;
    }
    if (this.checkIn && this.checkOut <= this.checkIn) {
      this.error = 'Check-out date must be after the check-in date.';
      return;
    }
    this.router.navigate([], {
      queryParams: {
        location: this.location.trim() || null,
        checkIn: this.checkIn || null,
        checkOut: this.checkOut || null,
      },
    });
  }

  clear(): void {
    this.router.navigate([], { queryParams: {} });
  }

  get hasDates(): boolean {
    return !!this.checkIn && !!this.checkOut;
  }

  private loadHotels(): void {
    this.loading = true;
    this.error = '';
    this.api.searchHotels(this.location.trim(), this.checkIn, this.checkOut).subscribe({
      next: (hotels) => {
        this.hotels = hotels;
        this.loading = false;
      },
      error: (err) => {
        this.hotels = [];
        this.error = errorMessage(err);
        this.loading = false;
      },
    });
  }
}
