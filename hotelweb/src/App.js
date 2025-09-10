import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";
import Home from "./components/Home";
import Booking from "./components/Booking";
import Checkout from "./components/Checkout";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import { MyUserContext, MyCartContext } from "./configs/MyContexts";
import { useReducer, useState } from "react";
import { MyUsrReducer } from "./reducers/MyUserReducer";
import { MyCartReducer } from "./reducers/MyCartReducer";
import '@fortawesome/fontawesome-free/css/all.min.css';

const App = () => {
  const [user, dispatch] = useReducer(MyUsrReducer, null);
  const [cartCounter, cartDispatch] = useReducer(MyCartReducer, 0);

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
            </Routes>
          </Container>

          <Footer />
        </BrowserRouter>
      </MyCartContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;