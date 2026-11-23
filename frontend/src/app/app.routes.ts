import { Routes } from '@angular/router';

import { SearchComponent } from './pages/search/search.component';
import { HotelDetailComponent } from './pages/hotel-detail/hotel-detail.component';
import { ConfirmationComponent } from './pages/confirmation/confirmation.component';
import { ManageBookingComponent } from './pages/manage-booking/manage-booking.component';

export const routes: Routes = [
  { path: '', component: SearchComponent, title: 'Find a hotel' },
  { path: 'hotels/:id', component: HotelDetailComponent, title: 'Hotel details' },
  { path: 'confirmation', component: ConfirmationComponent, title: 'Booking confirmed' },
  { path: 'manage', component: ManageBookingComponent, title: 'Manage booking' },
  { path: '**', redirectTo: '' },
];
