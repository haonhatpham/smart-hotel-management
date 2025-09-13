import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";
import Home from "./components/Home";
import Booking from "./components/Booking";
import Checkout from "./components/Checkout";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import { MyUserContext, MyCartContext } from "./configs/MyContexts";
import { useReducer,useEffect } from "react";
import { MyUsrReducer } from "./reducers/MyUserReducer";
import { MyCartReducer } from "./reducers/MyCartReducer";
import '@fortawesome/fontawesome-free/css/all.min.css';
import Login from "./components/Login";
import Register from "./components/Register";
import cookie from 'react-cookies'
import { authApis, endpoints } from "./configs/Api";
import Thankyou from "./components/thankyou";


const App = () => {
  const [user, dispatch] = useReducer(MyUsrReducer, null);
  const [cartCounter, cartDispatch] = useReducer(MyCartReducer, 0);
  const getUserFromToken = async () => {
  const token = cookie.load("token");

    if (token) {
      try {
        let user = await authApis().get(endpoints["profile"]);
        console.info(user.data);
        dispatch({ type: "login", payload: user.data });
      } catch (error) {
        console.error("Failed to fetch user profile with stored token:", error);
        dispatch({ type: "logout" });
      }
    }
  };

  useEffect(() => {
    getUserFromToken();
  }, []);

  return (
    <MyUserContext.Provider value={[user, dispatch]}>
      <MyCartContext.Provider value={[cartCounter, cartDispatch]}>
        <BrowserRouter>
          <Header />

          <Container>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/booking" element={<Booking />} />
              <Route path="/checkout" element={<Checkout />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/thankyou" element={<Thankyou />} />
              <Route path="/thankyou/result" element={<Thankyou />} />
            </Routes>
          </Container>

          <Footer />
        </BrowserRouter>
      </MyCartContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;