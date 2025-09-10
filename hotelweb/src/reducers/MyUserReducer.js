import cookie from 'react-cookies'
const MyUsrReducer = (currentState, action) => {
    switch (action.type) {
        case "login":
            return action.payload;
        case "logout":
            cookie.remove("token");
            return null;
        case "update":
            return { ...currentState, ...action.payload };
        default:
            return currentState;
    }
};

export { MyUsrReducer };
