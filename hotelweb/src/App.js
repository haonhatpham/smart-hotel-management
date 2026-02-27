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
import ForgotPassword from "./components/ForgotPassword";
import ResetPassword from "./components/ResetPassword";
import Profile from "./components/Profile";
import ReservationDetail from "./components/ReservationDetail";
import ReviewForm from "./components/ReviewForm";
import cookie from 'react-cookies'
import { authApis, endpoints, setAuthFailureHandler } from "./configs/Api";
import Thankyou from "./components/thankyou";
import Services from "./components/Services";
import ServiceDetail from "./components/ServiceDetail";
import Contact from "./components/Contact";
import About from "./components/About";
import Loyalty from "./components/Loyalty";
import ChatWidget from "./components/ChatWidget";
import NotFound from "./components/NotFound";


const App = () => {
  const [user, dispatch] = useReducer(MyUsrReducer, null);
  const [cartState, cartDispatch] = useReducer(MyCartReducer, { rooms: [], total: 0 });
  const getUserFromToken = async () => {
  const token = cookie.load("token");

    if (token) {
      try {
        let user = await authApis().get(endpoints["profile"]);
        dispatch({ type: "login", payload: user.data });
      } catch (error) {
        dispatch({ type: "logout" });
      }
    }
  };

  useEffect(() => {
    getUserFromToken();
  }, []);

  useEffect(() => {
    setAuthFailureHandler(() => {
      cookie.remove("token", { path: "/" });
      dispatch({ type: "logout" });
      const next = encodeURIComponent(window.location.pathname + window.location.search || "/");
      window.location.href = `/login?next=${next}`;
    });
    return () => setAuthFailureHandler(null);
  }, [dispatch]);

  return (
    <MyUserContext.Provider value={[user, dispatch]}>
      <MyCartContext.Provider value={[cartState, cartDispatch]}>
        <BrowserRouter>
          <Header />

          <Container>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/booking" element={<Booking />} />
              <Route path="/checkout" element={<Checkout />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/reservations/:id" element={<ReservationDetail />} />
              <Route path="/reservations/:id/review" element={<ReviewForm />} />
              <Route path="/thankyou" element={<Thankyou />} />
              <Route path="/thankyou/result" element={<Thankyou />} />
              <Route path="/services" element={<Services />} />
              <Route path="/services/:id" element={<ServiceDetail />} />
              <Route path="/contact" element={<Contact />} />
              <Route path="/about" element={<About />} />
              <Route path="/loyalty" element={<Loyalty />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </Container>

          <Footer />
          <ChatWidget />
        </BrowserRouter>
      </MyCartContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;