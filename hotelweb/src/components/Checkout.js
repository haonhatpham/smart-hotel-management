import { useContext, useEffect, useState } from "react";
import { Card, Button, Form, Row, Col } from "react-bootstrap";
import cookie from "react-cookies";
import { Link, useNavigate } from "react-router-dom";
import { MyUserContext } from "../configs/MyContexts";
import { authApis, endpoints } from "../configs/Api";

const Checkout = () => {
    const [cartItems, setCartItems] = useState([]);
    const [user] = useContext(MyUserContext);
    const [customerProfile, setCustomerProfile] = useState(null);
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
    const [isSubmitting, setIsSubmitting] = useState(false);


    const handleCheckout = async () => {
        if (!agree || isSubmitting) return;
        setIsSubmitting(true);
        try {
            // Chuẩn bị dữ liệu đặt phòng
            const reservationData = {
                fullName: form.fullName,
                email: form.email,
                phone: form.phone,
                address: form.address,
                note: form.note,
                checkIn: cartItems[0]?.checkIn,
                checkOut: cartItems[0]?.checkOut,
                createdAt: new Date().toISOString(),
                customerId: user?.id,
                status: "HELD",
                rooms: cartItems.map(item => ({
                    roomType: item.roomType,
                    roomId: item.id,
                    checkIn: item.checkIn,
                    checkOut: item.checkOut,
                    nights: item.nights,
                    guests: item.guests,
                    price: item.totalPrice
                })),
                totalAmount: total
            };

            // Tạo đơn đặt phòng
            const res = await authApis().post(endpoints['reservations'], reservationData);
            const reservation = res.data;
            const reservationId = reservation.id || reservation.reservationId || reservation.ID;
            if (!reservationId) throw new Error('Không lấy được mã đơn đặt phòng');

            // Gọi API thanh toán nếu chọn bank_card hoặc momo
            if (paymentMethod === 'bank_card' || paymentMethod === 'momo') {
                const paymentRes = await authApis().post(endpoints['payment-process'], {
                    reservationId,
                    amount: total,
                    paymentMethod: paymentMethod === 'bank_card' ? 'VNPAY' : 'MOMO'
                });
                const { paymentUrl, success, message } = paymentRes.data;
                if (success && paymentUrl) {
                    window.location.href = paymentUrl;
                } else {
                    alert(message || 'Không thể tạo thanh toán!');
                    setIsSubmitting(false);
                }
            } else if (paymentMethod === 'pay_at_hotel') {
                // Thanh toán tại quầy: chỉ xác nhận đặt phòng
                alert('Đặt phòng thành công! Vui lòng thanh toán tại quầy khi nhận phòng.');
                navigate('/thankyou');
                setIsSubmitting(false);
            }
        } catch (err) {
            alert('Có lỗi xảy ra: ' + (err?.response?.data?.message || err.message));
            setIsSubmitting(false);
        }
    };

    useEffect(() => {
        const token = cookie.load('token');
        if (!token) {
            navigate('/login?next=/checkout');
        }
    }, [navigate]);

    // Load giỏ hàng
    useEffect(() => {
        const cartKey = user ? `cart_${user.id}` : 'cart_guest';
        const cart = cookie.load(cartKey) || {};
        const items = Object.values(cart);
        setCartItems(items);
    }, [user]);

    // Đổ dữ liệu user vào form
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

    // Tải customer profile nếu đã đăng nhập
    useEffect(() => {
        const token = cookie.load('token');
        if (!token) return;
        authApis().get(endpoints['customer-profile'])
            .then(res => {
                setCustomerProfile(res.data);
                if (res.data) {
                    setForm(prev => ({ ...prev, address: res.data.address || "" }));
                }
            })
            .catch(() => setCustomerProfile(null));
    }, []);

    const total = cartItems.reduce((sum, x) => sum + (x.totalPrice || 0), 0);

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

            {cartItems.length === 0 ? (
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
                                                    onChange={e => setForm({ ...form, fullName: e.target.value })}
                                                    placeholder="VD: Nguyễn Văn A" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label>Số điện thoại</Form.Label>
                                                <Form.Control value={form.phone}
                                                    onChange={e => setForm({ ...form, phone: e.target.value })}
                                                    placeholder="VD: 09xx..." />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label>Email</Form.Label>
                                                <Form.Control type="email" value={form.email}
                                                    onChange={e => setForm({ ...form, email: e.target.value })}
                                                    placeholder="you@example.com" />
                                            </Form.Group>
                                        </Col>
                                        <Col md={6}>
                                            <Form.Group>
                                                <Form.Label>Địa chỉ</Form.Label>
                                                <Form.Control value={form.address}
                                                    onChange={e => setForm({ ...form, address: e.target.value })} />
                                            </Form.Group>
                                        </Col>
                                        <Col md={12}>
                                            <Form.Group>
                                                <Form.Label>Yêu cầu thêm</Form.Label>
                                                <Form.Control as="textarea" rows={3}
                                                    value={form.note}
                                                    onChange={e => setForm({ ...form, note: e.target.value })}
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
                                <div className="d-flex justify-content-between align-items-center mt-3">
                                    <span className="h6 mb-0">Tổng cộng</span>
                                    <span className="h5 text-primary fw-bold mb-0">{total.toLocaleString()} VND</span>
                                </div>
                                <div className="text-muted small">Đã bao gồm thuế và phí</div>
                                <Button
                                    variant="success"
                                    size="lg"
                                    className="w-100 mt-3"
                                    disabled={!agree || isSubmitting}
                                    onClick={handleCheckout}
                                >
                                    {isSubmitting ? 'Đang xử lý...' : 'Thực hiện đặt phòng'}
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