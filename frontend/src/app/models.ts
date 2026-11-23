// Shapes of the JSON returned by the Spring Boot API

export interface HotelSummary {
  id: number;
  name: string;
  location: string;
  description: string;
  amenities: string[];
  roomName: string;
  pricePerNight: number;
  availableRooms: number | null; // null when searched without dates
  nights: number | null;
  totalAmount: number | null;
}

export interface HotelDetails {
  id: number;
  name: string;
  location: string;
  address: string;
  description: string;
  amenities: string[];
  room: {
    name: string;
    pricePerNight: number;
    totalRooms: number;
    maxGuests: number;
  };
}

export interface AlternativeStay {
  checkIn: string;
  checkOut: string;
  totalAmount: number;
}

export interface Availability {
  hotelId: number;
  checkIn: string;
  checkOut: string;
  nights: number;
  pricePerNight: number;
  totalAmount: number;
  availableRooms: number;
  alternativeStays: AlternativeStay[];
}

export interface BookingRequest {
  hotelId: number;
  guestName: string;
  guestEmail: string;
  checkIn: string;
  checkOut: string;
}

export interface Booking {
  bookingReference: string;
  status: 'CONFIRMED' | 'CANCELLED';
  hotelId: number;
  hotelName: string;
  hotelLocation: string;
  roomName: string;
  guestName: string;
  guestEmail: string;
  checkIn: string;
  checkOut: string;
  nights: number;
  pricePerNight: number;
  totalAmount: number;
  createdAt: string;
  cancelledAt: string | null;
}

export interface ApiError {
  status: number;
  message: string;
  fieldErrors: Record<string, string> | null;
}
