const MyCartReducer = (currentState, action) => {
    switch (action.type) {
        case "inc":
            return currentState + 1;
        case "dec":
            return currentState > 0 ? currentState - 1 : 0;
        case "set":
            return action.payload;
        case "reset":
            return 0;
        default:
            return currentState;
    }
};

export { MyCartReducer };
