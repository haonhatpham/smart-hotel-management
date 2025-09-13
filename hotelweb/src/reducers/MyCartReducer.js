const calcNights = (checkIn, checkOut) => {
    const nights =
        (new Date(checkOut) - new Date(checkIn)) / (1000 * 60 * 60 * 24);
    return nights > 0 ? nights : 1;
};

const calcTotal = (rooms) => {
    return rooms.reduce((sum, room) => {
        const nights = calcNights(room.checkIn, room.checkOut);

        const roomCost = nights * room.price;

        const servicesCost = room.services.reduce(
            (s, service) => s + service.price * nights,
            0
        );

        return sum + roomCost + servicesCost;
    }, 0);
};

const MyCartReducer = (state, action) => {
    switch (action.type) {
        case "add":
            return {
                ...state,
                rooms: [...state.rooms, { ...action.payload, services: action.payload.services || [] }],
                total: calcTotal([...state.rooms, { ...action.payload, services: action.payload.services || [] }]),
            };

        case "delete":
            let filteredRooms;
            if (typeof action.payload === 'object' && action.payload.id) {
                // Xóa phòng dựa trên ID, checkIn và checkOut
                filteredRooms = state.rooms.filter(r => 
                    !(r.id === action.payload.id && 
                      r.checkIn === action.payload.checkIn && 
                      r.checkOut === action.payload.checkOut)
                );
            } else {
                // Xóa phòng dựa trên ID (backward compatibility)
                filteredRooms = state.rooms.filter(r => r.id !== action.payload);
            }
            return {
                ...state,
                rooms: filteredRooms,
                total: calcTotal(filteredRooms),
            };

        case "reset":
            return { rooms: [], total: 0 };

        default:
            return state;
    }
};

export { MyCartReducer };
