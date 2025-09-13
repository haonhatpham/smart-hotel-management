import { useContext, useEffect, useMemo, useState } from "react";
import { Card, Row, Col, Button, Badge, Spinner } from "react-bootstrap";
import { MyUserContext } from "../configs/MyContexts";
import { Link, useNavigate } from "react-router-dom";
import cookie from 'react-cookies';
import Apis, { authApis, endpoints } from "../configs/Api";

const Profile = () => {
    const [user] = useContext(MyUserContext);
    const [profile, setProfile] = useState(null);
    const [loadingProfile, setLoadingProfile] = useState(true);
    const [reservations, setReservations] = useState([]);
    const [loadingRes, setLoadingRes] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const token = cookie.load('token');
        if (!token) {
            navigate('/login?next=/profile');
        }
    }, [navigate]);

    useEffect(() => {
        const token = cookie.load('token');
        if (!token) return;

        setLoadingProfile(true);
        authApis().get(endpoints['customer-profile'])
            .then(res => setProfile(res.data))
            .catch(() => setProfile(null))
            .finally(() => setLoadingProfile(false));
    }, []);

    useEffect(() => {
        if (!user) return;
        setLoadingRes(true);
        const cid = String(user.id).trim();
        const url = `${endpoints['reservations']}?customerId=${cid}`;
        console.log('[Profile] user.id =', user.id, 'Request URL =', url);
        Apis.get(url)
            .then(res => {
                console.log('[Profile] reservations API status:', res.status, 'data:', res.data);
                const data = res.data;
                setReservations(Array.isArray(data) ? data : (data?.items || []));
            })
            .catch(err => {
                console.error('[Profile] reservations API error:', err);
                setReservations([])
            })
            .finally(() => setLoadingRes(false));
    }, [user]);

    const headerInfo = useMemo(() => ({
        fullName: user?.fullName || '—',
        email: user?.email || '—',
        phone: user?.phone || '—',
        address: profile?.address || '—',
        dob: profile?.dob ? new Date(profile.dob).toLocaleDateString() : '—',
        points: profile?.loyaltyPoint ?? '—',
        notes: profile?.notes || ''
    }), [user, profile]);

    return (
        <div className="container my-4">
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">Hồ sơ của bạn</h3>
                <div>
                    <Link to="/booking" className="btn btn-outline-secondary btn-sm">Tiếp tục đặt phòng</Link>
                </div>
            </div>

            <Row className="g-3">
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <div className="fw-bold mb-2">Thông tin hồ sơ</div>
                            {loadingProfile ? (
                                <div className="text-center py-3"><Spinner animation="border" size="sm" /></div>
                            ) : (
                                <div className="small">
                                    <div>Họ tên: {headerInfo.fullName}</div>
                                    <div>Email: {headerInfo.email}</div>
                                    <div>SĐT: {headerInfo.phone}</div>
                                    <div>Địa chỉ: {headerInfo.address}</div>
                                    <div>Ngày sinh: {headerInfo.dob}</div>
                                    <div>Điểm tích lũy: {headerInfo.points}</div>
                                    {headerInfo.notes && <div>Ghi chú: {headerInfo.notes}</div>}
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>

                <Col md={8}>
                    <Card>
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-center mb-2">
                                <div className="fw-bold">Lịch sử đặt phòng</div>
                                {loadingRes && <Spinner animation="border" size="sm" />}
                            </div>

                            {(!loadingRes && reservations.length === 0) ? (
                                <div className="text-muted small">Chưa có đặt phòng nào.</div>
                            ) : (
                                <div className="vstack gap-2">
                                    {reservations.map(r => (
                                        <div key={r.id} className="border rounded p-2 d-flex justify-content-between align-items-center">
                                            <div className="small">
                                                <div className="fw-bold">Mã đặt phòng: #{r.id} {r.status && <Badge bg="secondary" className="ms-1">{r.status}</Badge>}</div>
                                                <div>{r.checkIn} → {r.checkOut} • {r.guests || 2} khách</div>
                                                {r.totalAmount && <div className="text-primary">Tổng: {r.totalAmount.toLocaleString()} VND</div>}
                                            </div>
                                            <div className="d-flex gap-2">
                                                <Button size="sm" variant="outline-secondary" onClick={() => navigate(`/reservations/${r.id}`)}>Chi tiết</Button>
                                                {(r.status === 'CHECKED_OUT' || r.status === 'COMPLETED') && (
                                                    <Button size="sm" variant="success" onClick={() => navigate(`/reservations/${r.id}/review`)}>Đánh giá</Button>
                                                )}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </div>
    );
};

export default Profile;


