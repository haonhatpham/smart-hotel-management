const MyUsrReducer = (currentState, action) => {
    switch (action.type) {
        case "login":
            return action.payload;
        case "logout":
            return null;
        case "update":
            return { ...currentState, ...action.payload };
        default:
            return currentState;
    }
};

export { MyUsrReducer };
