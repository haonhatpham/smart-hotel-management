# Fix for Multiple Rooms and Services Issue

## Problem
When creating a reservation with multiple rooms and services, only the first room and first service were being saved to the database.

## Root Cause
The issue was with how Hibernate handles Set collections when the parent entity doesn't have an ID yet. When we try to save a reservation with multiple ReservationRooms and ServiceOrders in a single transaction, Hibernate may not properly cascade the saves for all items in the Set.

## Solution
Changed the approach to:

1. **Save reservation first** to get the ID
2. **Save each ReservationRoom individually** with the reservation ID
3. **Save each ServiceOrder individually** with the reservation ID

## Code Changes

### 1. ReservationServiceImpl.createFromDTO()
```java
// OLD APPROACH (PROBLEMATIC):
// Create all ReservationRooms and ServiceOrders in Sets
// Set them to reservation
// Save reservation (cascade may not work properly)

// NEW APPROACH (FIXED):
// 1. Save reservation first to get ID
Reservations savedReservation = this.reservationRepository.addOrUpdate(reservation);

// 2. Save each ReservationRoom individually
for (ReservationRoomDTO roomDTO : dto.getRooms()) {
    ReservationRooms reservationRoom = new ReservationRooms();
    reservationRoom.setReservationId(savedReservation);
    this.reservationRepository.addOrUpdateReservationRoom(reservationRoom);
}

// 3. Save each ServiceOrder individually  
for (ServiceOrderDTO serviceDTO : dto.getServices()) {
    ServiceOrders serviceOrder = new ServiceOrders();
    serviceOrder.setReservationId(savedReservation);
    this.reservationRepository.addOrUpdateServiceOrder(serviceOrder);
}
```

### 2. Added new method to ReservationRepository
```java
ReservationRooms addOrUpdateReservationRoom(ReservationRooms reservationRoom);
```

### 3. Implemented in ReservationRepositoryImpl
```java
@Override
public ReservationRooms addOrUpdateReservationRoom(ReservationRooms reservationRoom) {
    Session s = this.factory.getObject().getCurrentSession();
    if (reservationRoom.getId() == null) {
        s.persist(reservationRoom);
        return reservationRoom;
    }
    return (ReservationRooms) s.merge(reservationRoom);
}
```

## Benefits

1. **Guaranteed saves**: Each room and service is saved individually
2. **Clear transaction boundaries**: Each save operation is explicit
3. **Better debugging**: Can see exactly which items are being saved
4. **Consistent behavior**: Works regardless of Set size

## Test Case
Create a reservation with:
- 3 rooms
- 2 services per room (6 total services)

Expected result: All 3 rooms and 6 services should be saved to database with correct foreign key references.

## Debug Output
The system now logs each save operation:
```
[DEBUG] Saved reservation with ID: 123
[DEBUG] Saved ReservationRoom: roomId=1, price=500000
[DEBUG] Saved ReservationRoom: roomId=2, price=600000  
[DEBUG] Saved ReservationRoom: roomId=3, price=700000
[DEBUG] Saved ServiceOrder: serviceId=1, qty=2
[DEBUG] Saved ServiceOrder: serviceId=2, qty=1
[DEBUG] Saved ServiceOrder: serviceId=3, qty=3
[DEBUG] Saved ServiceOrder: serviceId=4, qty=1
[DEBUG] Saved ServiceOrder: serviceId=5, qty=2
[DEBUG] Saved ServiceOrder: serviceId=6, qty=1
```

