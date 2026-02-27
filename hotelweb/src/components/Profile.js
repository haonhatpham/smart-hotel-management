import { useContext, useEffect, useMemo, useState } from "react";
import { Card, Row, Col, Button, Badge, Spinner } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { MyUserContext } from "../configs/MyContexts";
import { Link, useNavigate } from "react-router-dom";
import cookie from "react-cookies";
import { authApis, endpoints } from "../configs/Api";

const PAGE_SIZE = 6;

const Profile = () => {
    const { t } = useTranslation();
    const [user] = useContext(MyUserContext);
    const [profile, setProfile] = useState(null);
    const [loadingProfile, setLoadingProfile] = useState(true);
    const [reservations, setReservations] = useState([]);
    const [totalRes, setTotalRes] = useState(0);
    const [page, setPage] = useState(1);
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
        const url = `${endpoints['reservations']}?page=${page}`;
        authApis().get(url)
            .then(res => {
                const data = res.data;
                if (data && typeof data === 'object' && 'items' in data) {
                    setReservations(data.items || []);
                    setTotalRes(Number(data.total) || 0);
                } else {
                    const list = Array.isArray(data) ? data : [];
                    setReservations(list);
                    setTotalRes(list.length);
                }
            })
            .catch(() => {
                setReservations([]);
                setTotalRes(0);
            })
            .finally(() => setLoadingRes(false));
    }, [user, page]);

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
                <h3 className="mb-0">{t("profile.title")}</h3>
                <div>
                    <Link to="/booking" className="btn btn-outline-secondary btn-sm">{t("profile.continueBooking")}</Link>
                </div>
            </div>

            <Row className="g-3">
                <Col md={4}>
                    <Card>
                        <Card.Body>
                            <div className="fw-bold mb-2">{t("profile.profileInfo")}</div>
                            {loadingProfile ? (
                                <div className="text-center py-3"><Spinner animation="border" size="sm" /></div>
                            ) : (
                                <div className="small">
                                    <div>{t("profile.fullName")}: {headerInfo.fullName}</div>
                                    <div>{t("profile.email")}: {headerInfo.email}</div>
                                    <div>{t("profile.phone")}: {headerInfo.phone}</div>
                                    <div>{t("profile.address")}: {headerInfo.address}</div>
                                    <div>{t("profile.dob")}: {headerInfo.dob}</div>
                                    <div>{t("profile.points")}: {headerInfo.points} <Link to="/loyalty" className="small ms-1">{t("profile.viewLoyalty")}</Link></div>
                                    {headerInfo.notes && <div>{t("profile.notes")}: {headerInfo.notes}</div>}
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>

                <Col md={8}>
                    <Card>
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-center mb-2">
                                <div className="fw-bold">{t("profile.reservationHistory")}</div>
                                {loadingRes && <Spinner animation="border" size="sm" />}
                            </div>

                            {(!loadingRes && reservations.length === 0) ? (
                                <div className="text-muted small">{t("profile.noReservations")}</div>
                            ) : (
                                <>
                                    <div className="vstack gap-2">
                                        {reservations.map(r => (
                                            <div key={r.id} className="border rounded p-2 d-flex justify-content-between align-items-center">
                                                <div className="small">
                                                    <div className="fw-bold">{t("profile.reservationCode")}: #{r.id} {r.status && <Badge bg="secondary" className="ms-1">{r.status}</Badge>}</div>
                                                    <div>{r.checkIn} → {r.checkOut} • {r.guests || 2} {t("profile.guests")}</div>
                                                    {r.totalAmount && <div className="text-primary">{t("profile.total")}: {r.totalAmount.toLocaleString()} VND</div>}
                                                </div>
                                                <div className="d-flex gap-2">
                                                    <Button size="sm" variant="outline-secondary" onClick={() => navigate(`/reservations/${r.id}`)}>{t("profile.detail")}</Button>
                                                    {(r.status === "CHECKED_OUT" || r.status === "COMPLETED") && (
                                                        <Button size="sm" variant="success" onClick={() => navigate(`/reservations/${r.id}/review`)}>{t("profile.review")}</Button>
                                                    )}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                    {totalRes > PAGE_SIZE && (
                                        <div className="d-flex justify-content-between align-items-center mt-3">
                                            <span className="small text-muted">
                                                {((page - 1) * PAGE_SIZE) + 1}–{Math.min(page * PAGE_SIZE, totalRes)} / {totalRes}
                                            </span>
                                            <div className="d-flex gap-1">
                                                <Button size="sm" variant="outline-secondary" disabled={page <= 1} onClick={() => setPage(p => Math.max(1, p - 1))}>
                                                    {t("profile.prev")}
                                                </Button>
                                                <Button size="sm" variant="outline-secondary" disabled={page * PAGE_SIZE >= totalRes} onClick={() => setPage(p => p + 1)}>
                                                    {t("profile.next")}
                                                </Button>
                                            </div>
                                        </div>
                                    )}
                                </>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </div>
    );
};

export default Profile;


