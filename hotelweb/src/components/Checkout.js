import { useEffect, useState } from "react";
import { Card, Button } from "react-bootstrap";
import cookie from "react-cookies";
import { Link, useNavigate } from "react-router-dom";

const Checkout = () => {
    const [cartItems, setCartItems] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const cart = cookie.load("cart") || {};
        const items = Object.values(cart);
        setCartItems(items);
    }, []);

    const total = cartItems.reduce((sum, x) => sum + (x.totalPrice || 0), 0);

    return (
        <div className="container my-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">Xác nhận đặt phòng</h3>
                <div>
                    <Button variant="outline-secondary" size="sm" className="me-2" onClick={() => navigate(-1)}>
                        Quay lại
                    </Button>
                </div>
            </div>

            {cartItems.length === 0 ? (
                <Card className="p-4 text-center">
                    <div>Chưa có phòng trong giỏ.</div>
                    <Link to="/booking" className="btn btn-primary mt-3">Quay lại tìm phòng</Link>
                </Card>
            ) : (
                <>
                    <Card className="mb-3">
                        <Card.Body>
                            {cartItems.map((r) => (
                                <div key={r.id + '_' + r.checkIn} className="border-bottom py-2">
                                    <div className="d-flex justify-content-between">
                                        <div>
                                            <div className="fw-bold">{r.roomType}</div>
                                            <small className="text-muted">{r.checkIn} → {r.checkOut} • {r.nights} đêm • {r.guests} khách</small>
                                        </div>
                                        <div className="fw-bold text-primary">{r.totalPrice?.toLocaleString()} VND</div>
                                    </div>
                                </div>
                            ))}
                        </Card.Body>
                    </Card>

                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <span className="h5 mb-0">Tổng cộng:</span>
                        <span className="h4 text-primary fw-bold mb-0">{total.toLocaleString()} VND</span>
                    </div>

                    <Button variant="success" size="lg" className="w-100" onClick={() => alert("Giả lập: Thanh toán thành công!")}>Thanh toán</Button>
                </>
            )}
        </div>
    );
};

export default Checkout;


