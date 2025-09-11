import cookie from 'react-cookies'
const MyUsrReducer = (currentState, action) => {
    switch (action.type) {
        case "login":
            return action.payload;
        case "logout":
            cookie.remove("token");
            // Clear carts on logout
            try {
                // Remove guest cart key if any
                cookie.remove('cart_guest');
                // Optionally clear any legacy cart key
                cookie.remove('cart');
            } catch (e) {}
            return null;
        case "update":
            return { ...currentState, ...action.payload };
        default:
            return currentState;
    }
};

export { MyUsrReducer };
