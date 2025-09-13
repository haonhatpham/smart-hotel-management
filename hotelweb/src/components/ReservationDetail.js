import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Card, Row, Col, Badge, Table, Button, Spinner } from "react-bootstrap";
import { endpoints } from "../configs/Api";
import Apis from "../configs/Api";

const ReservationDetail = () => {
    const { id } = useParams();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const url = `${endpoints['reservations']}/${id}`;
        Apis.get(url)
            .then(res => setData(res.data))
            .catch(() => setData(null))
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) return (
        <div className="container my-4 text-center"><Spinner animation="border" /></div>
    );

    if (!data) return (
        <div className="container my-4">Không tìm thấy đơn đặt phòng.
            <div className="mt-2"><Button onClick={() => navigate(-1)}>Quay lại</Button></div>
        </div>
    );

    const { reservation, rooms = [], services = [], invoice } = data;

    const nights = reservation?.checkIn && reservation?.checkOut
        ? Math.ceil((new Date(reservation.checkOut) - new Date(reservation.checkIn)) / (1000*60*60*24))
        : 1;

    const roomsTotal = rooms.reduce((sum, rr) => sum + (Number(rr.pricePerNight) * nights || 0), 0);
    const servicesTotal = services.reduce((sum, s) => sum + (Number(s.amount) || 0), 0);
    const total = invoice?.totalAmount ?? (roomsTotal + servicesTotal);

    return (
        <div className="container my-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">Chi tiết đặt phòng #{reservation?.id} {reservation?.status && <Badge bg="secondary">{reservation.status}</Badge>}</h3>
                <Button variant="outline-secondary" size="sm" onClick={() => navigate(-1)}>Quay lại</Button>
            </div>

            <Row className="g-3">
                <Col md={6}>
                    <Card>
                        <Card.Body>
                            <div className="fw-bold mb-2">Thông tin</div>
                            <div className="small">Nhận phòng: {reservation?.checkIn ? new Date(reservation.checkIn).toLocaleDateString() : '—'}</div>
                            <div className="small">Trả phòng: {reservation?.checkOut ? new Date(reservation.checkOut).toLocaleDateString() : '—'}</div>
                            <div className="small">Số đêm: {nights}</div>
                        </Card.Body>
                    </Card>
                </Col>

                <Col md={6}>
                    <Card>
                        <Card.Body>
                            <div className="fw-bold mb-2">Tổng tiền</div>
                            <div className="small">Phòng: {roomsTotal.toLocaleString()} VND</div>
                            <div className="small">Dịch vụ: {servicesTotal.toLocaleString()} VND</div>
                            <div className="h5 mt-2 text-primary">Thành tiền: {Number(total).toLocaleString()} VND</div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            <Card className="mt-3">
                <Card.Body>
                    <div className="fw-bold mb-2">Phòng</div>
                    <Table size="sm" bordered responsive>
                        <thead>
                            <tr>
                                <th>Phòng</th>
                                <th>Loại phòng</th>
                                <th>Giá/đêm</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rooms.map(rr => (
                                <tr key={rr.id}>
                                    <td>{rr.roomId?.roomNumber}</td>
                                    <td>{rr.roomId?.roomTypeId?.name}</td>
                                    <td>{Number(rr.pricePerNight).toLocaleString()} VND</td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </Card.Body>
            </Card>

            <Card className="mt-3">
                <Card.Body>
                    <div className="fw-bold mb-2">Dịch vụ</div>
                    {services.length === 0 ? (
                        <div className="text-muted small">Không có dịch vụ.</div>
                    ) : (
                        <Table size="sm" bordered responsive>
                            <thead>
                                <tr>
                                    <th>Dịch vụ</th>
                                    <th>SL</th>
                                    <th>Đơn giá</th>
                                    <th>Thành tiền</th>
                                </tr>
                            </thead>
                            <tbody>
                                {services.map(s => (
                                    <tr key={s.id}>
                                        <td>{s.serviceId?.name}</td>
                                        <td>{s.qty}</td>
                                        <td>{Number(s.unitPrice).toLocaleString()} VND</td>
                                        <td>{Number(s.amount).toLocaleString()} VND</td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
};

export default ReservationDetail;


