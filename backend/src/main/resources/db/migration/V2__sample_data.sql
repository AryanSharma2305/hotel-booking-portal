-- Sample hotels. Room inventory for these is generated automatically by the
-- application at startup (see InventoryService), for the booking window.

INSERT INTO hotel (id, name, location, address, description) VALUES
(1, 'Sea Breeze Residency', 'Mumbai', 'Juhu Tara Road, Juhu, Mumbai 400049',
 'A calm beach-facing hotel a short walk from Juhu Beach, popular with families and business travellers.'),
(2, 'Palm Grove Beach Resort', 'Goa', 'Calangute-Baga Road, Calangute, Goa 403516',
 'Cottage-style resort set in a coconut grove, five minutes from Calangute beach.'),
(3, 'Heritage Haveli', 'Jaipur', 'Near Hawa Mahal, Old City, Jaipur 302002',
 'A restored 19th-century haveli with only a few rooms, hand-painted walls and a rooftop view of the Pink City.'),
(4, 'Lakeview Palace Inn', 'Udaipur', 'Lal Ghat, Udaipur 313001',
 'Lake Pichola views from every room and evening boat rides arranged at the front desk.'),
(5, 'Snow Peak Lodge', 'Manali', 'Old Manali Road, Manali 175131',
 'Wooden mountain lodge with a fireplace lounge, ideal for treks to Solang Valley and Rohtang.'),
(6, 'Backwater Retreat', 'Kochi', 'Fort Kochi Beach Road, Kochi 682001',
 'Quiet retreat near the Chinese fishing nets, with Kerala breakfast and cycle hire included.');

INSERT INTO hotel_amenity (hotel_id, amenity) VALUES
(1, 'Free Wi-Fi'), (1, 'Air conditioning'), (1, 'Restaurant'), (1, 'Airport shuttle'),
(2, 'Free Wi-Fi'), (2, 'Swimming pool'), (2, 'Beach access'), (2, 'Breakfast included'),
(3, 'Free Wi-Fi'), (3, 'Rooftop cafe'), (3, 'Heritage walk'),
(4, 'Free Wi-Fi'), (4, 'Lake view'), (4, 'Restaurant'), (4, 'Room service'),
(5, 'Free Wi-Fi'), (5, 'Room heater'), (5, 'Mountain view'), (5, 'Parking'),
(6, 'Free Wi-Fi'), (6, 'Breakfast included'), (6, 'Bicycle hire'), (6, 'Garden');

-- Heritage Haveli has only 2 rooms on purpose, so "sold out" is easy to test.
INSERT INTO room_type (id, hotel_id, name, price_per_night, total_rooms, max_guests) VALUES
(1, 1, 'Deluxe Sea View Room', 5500.00, 10, 2),
(2, 2, 'Garden Cottage',       4200.00,  8, 3),
(3, 3, 'Heritage Suite',       6800.00,  2, 2),
(4, 4, 'Lake View Room',       7500.00,  6, 2),
(5, 5, 'Mountain Cabin',       3200.00,  5, 4),
(6, 6, 'Courtyard Room',       3900.00,  7, 2);
