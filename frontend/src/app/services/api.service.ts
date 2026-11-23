import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Availability, Booking, BookingRequest, HotelDetails, HotelSummary } from '../models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  searchHotels(location: string, checkIn: string, checkOut: string): Observable<HotelSummary[]> {
    let params = new HttpParams();
    if (location) params = params.set('location', location);
    if (checkIn && checkOut) params = params.set('checkIn', checkIn).set('checkOut', checkOut);
    return this.http.get<HotelSummary[]>('/api/hotels', { params });
  }

  getLocations(): Observable<string[]> {
    return this.http.get<string[]>('/api/hotels/locations');
  }

  getHotel(id: number): Observable<HotelDetails> {
    return this.http.get<HotelDetails>(`/api/hotels/${id}`);
  }

  checkAvailability(id: number, checkIn: string, checkOut: string): Observable<Availability> {
    const params = new HttpParams().set('checkIn', checkIn).set('checkOut', checkOut);
    return this.http.get<Availability>(`/api/hotels/${id}/availability`, { params });
  }

  createBooking(request: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>('/api/bookings', request);
  }

  getBooking(reference: string, email: string): Observable<Booking> {
    const params = new HttpParams().set('email', email);
    return this.http.get<Booking>(`/api/bookings/${encodeURIComponent(reference)}`, { params });
  }

  cancelBooking(reference: string, email: string): Observable<Booking> {
    return this.http.post<Booking>(`/api/bookings/${encodeURIComponent(reference)}/cancel`, { email });
  }
}

/** Readable message from any failed API call. */
export function errorMessage(err: HttpErrorResponse): string {
  if (err.status === 0) {
    return 'Cannot reach the server. Please check that the backend is running.';
  }
  return err.error?.message ?? 'Something went wrong. Please try again.';
}

/** Field errors from a 400 validation response, e.g. { guestEmail: 'Enter a valid email address' }. */
export function fieldErrors(err: HttpErrorResponse): Record<string, string> {
  return err.error?.fieldErrors ?? {};
}

/** Today's date as yyyy-MM-dd in the user's own time zone (used as the minimum for date pickers). */
export function todayIso(): string {
  return toIsoDate(new Date());
}

export function addDays(isoDate: string, days: number): string {
  const [y, m, d] = isoDate.split('-').map(Number);
  return toIsoDate(new Date(y, m - 1, d + days));
}

function toIsoDate(date: Date): string {
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  return `${date.getFullYear()}-${mm}-${dd}`;
}
