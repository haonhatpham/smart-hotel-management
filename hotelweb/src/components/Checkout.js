import { useContext, useEffect, useState } from "react";
import { Card, Button, Form, Row, Col } from "react-bootstrap";
import cookie from "react-cookies";
import { Link, useNavigate } from "react-router-dom";
import { MyUserContext, MyCartContext } from "../configs/MyContexts";
import { authApis, endpoints } from "../configs/Api";

const Checkout = () => {
    const [user] = useContext(MyUserContext);
    const [cartState, cartDispatch] = useContext(MyCartContext);
    const [, setCustomerProfile] = useState(null);
    const [agree, setAgree] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState('bank_card');
    const [form, setForm] = useState({
        fullName: "",
        email: "",
        phone: "",
        address: "",
        note: ""
    });
    const navigate = useNavigate();

    useEffect(() => {
        const token = cookie.load('token');
        if (!token) {
            navigate('/login?next=/checkout');
        }
    }, [navigate]);


    useEffect(() => {
        if (user) {
            setForm(prev => ({
                ...prev,
                fullName: user.fullName || "",
                email: user.email || "",
                phone: user.phone || ""
            }));
        }
    }, [user]);


    useEffect(() => {
        const token = cookie.load('token');
        if (!token) return;
        authApis().get(endpoints['customer-profile'])
            .then(res => {
                setCustomerProfile(res.data);
                if (res.data) {
                    setForm(prev => ({...prev, address: res.data.address || ""}));
                }
            })
            .catch(() => setCustomerProfile(null));
    }, []);

    const total = cartState.total;

    const handlePayment = async () => {
        try {

            alert("Thanh toán thành công! Đơn đặt phòng đã được tạo.");
        
            cartDispatch({ type: "reset" });
            
            const cartKey = user ? `cart_${user.id}` : 'cart_guest';
            cookie.remove(cartKey);

            navigate('/');
        } catch (error) {
            alert("Có lỗi xảy ra khi thanh toán. Vui lòng thử lại.");
        }
    };

    return (
        <div className="container my-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">Thông tin đặt phòng</h3>
                <div>
                    <Button variant="outline-secondary" size="sm" className="me-2" onClick={() => navigate(-1)}>
                        Quay lại
                    </Button>
                </div>
            </div>

            {cartState.rooms.length === 0 ? (
                <Card className="p-4 text-center">
                    <div>Chưa có phòng trong giỏ.</div>
                    <Link to="/booking" className="btn btn-primary mt-3">Quay lại tìm phòng</Link>
                </Card>
            ) : (
                <Row className="g-3">
                    <Col md={8}>
                        <Card className="mb-3">
                            <Card.Body>
                                <div className="mb-3 fw-bold">Thông tin người đặt phòng</div>
                                <Form>
                                    <Row className="g-3">
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label>Họ và tên</Form.Label>
                                                <Form.Control value={form.fullName}
                                                    onChange={e => setForm({...form, fullName: e.target.value})}
                                                    placeholder="VD: Nguyễn Văn A" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label>Số điện thoại</Form.Label>
                                                <Form.Control value={form.phone}
                                                    onChange={e => setForm({...form, phone: e.target.value})}
                                                    placeholder="VD: 09xx..." />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label>Email</Form.Label>
                                                <Form.Control type="email" value={form.email}
                                                    onChange={e => setForm({...form, email: e.target.value})}
                                                    placeholder="you@example.com" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label>Địa chỉ</Form.Label>
                                                <Form.Control value={form.address}
                                                    onChange={e => setForm({...form, address: e.target.value})} />
                                            </Form.Group>
                                        </Col>
                                        <Col md={12}>
                                            <Form.Group>
                                                <Form.Label>Yêu cầu thêm</Form.Label>
                                                <Form.Control as="textarea" rows={3}
                                                    value={form.note}
                                                    onChange={e => setForm({...form, note: e.target.value})}
                                                    placeholder="Ghi chú cho khách sạn (nếu có)" />
                                            </Form.Group>
                                        </Col>
                                
                                    </Row>
                                </Form>
                            </Card.Body>
                        </Card>

                        <Card className="mb-3">
                            <Card.Body>
                                <div className="mb-2 fw-bold">Chính sách đặt phòng</div>
                                <ul className="mb-0 text-muted">
                                    <li>Không hoàn tiền đối với giá ưu đãi.</li>
                                    <li>Hủy/đổi muộn hoặc không đến: tính phí toàn bộ đặt phòng.</li>
                                    <li>Giá đã bao gồm ăn sáng và thuế, phí.</li>
                                </ul>
                            </Card.Body>
                        </Card>

                        <Card className="mb-3">
                            <Card.Body>
                                <div className="mb-2 fw-bold">Phương thức thanh toán</div>
                                <Form.Select value={paymentMethod} onChange={e => setPaymentMethod(e.target.value)}>
                                    <option value="bank_card">Thẻ ngân hàng (Visa/Master/JCB/Napas)</option>
                                    <option value="momo">Ví điện tử MoMo</option>
                                    <option value="pay_at_hotel">Thanh toán trực tiếp tại quầy</option>
                                </Form.Select>
                                <div className="text-muted small mt-2">
                                    {paymentMethod === 'bank_card' && 'Thanh toán an toàn qua cổng thẻ nội địa/quốc tế.'}
                                    {paymentMethod === 'momo' && 'Bạn sẽ được chuyển sang ứng dụng MoMo để hoàn tất.'}
                                    {paymentMethod === 'pay_at_hotel' && 'Giữ phòng và thanh toán khi nhận phòng tại quầy.'}
                                </div>
                                <Form.Check className="mt-3"
                                    type="checkbox"
                                    label="Tôi đồng ý với điều khoản đặt phòng"
                                    checked={agree}
                                    onChange={e => setAgree(e.target.checked)}
                                />
                            </Card.Body>
                        </Card>
                    </Col>

                    <Col md={4}>
                        <Card className="mb-3">
                            <Card.Body>
                                {cartState.rooms.map((r) => (
                                    <div key={r.id + '_' + r.checkIn} className="border-bottom py-2">
                                        <div className="d-flex justify-content-between align-items-start">
                                            <div className="flex-grow-1 me-3">
                                                <div className="fw-bold">{r.roomType}</div>
                                                <small className="text-muted">{r.checkIn} → {r.checkOut} • {r.nights} đêm • {r.guests} khách</small>
                                            </div>
                                            <div className="fw-bold text-primary text-nowrap text-end">
                                                {(r.price * r.nights + (r.services?.reduce((sum, s) => sum + s.totalPrice, 0) || 0)).toLocaleString()} VND
                                            </div>
                                        </div>
                                    </div>
                                ))}
                                <div className="d-flex justify-content-between align-items-center mt-3">
                                    <span className="h6 mb-0">Tổng cộng</span>
                                    <span className="h5 text-primary fw-bold mb-0 text-nowrap">{total.toLocaleString()} VND</span>
                                </div>
                                <div className="text-muted small">Đã bao gồm thuế và phí</div>
                                <Button
                                    variant="success"
                                    size="lg"
                                    className="w-100 mt-3"
                                    disabled={!agree}
                                    onClick={handlePayment}
                                >
                                    Thực hiện đặt phòng
                                </Button>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            )}
        </div>
    );
};

export default Checkout;


