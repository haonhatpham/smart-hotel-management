import { useEffect, useState } from "react";
import { Card, Col, Container, Row, Spinner, Table } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { authApis, endpoints } from "../configs/Api";

const tierInfo = {
    BRONZE: { label: "Đồng", color: "#cd7f32", icon: "fa-medal", next: "Silver", nextAt: 1000 },
    SILVER: { label: "Bạc", color: "#c0c0c0", icon: "fa-medal", next: "Gold", nextAt: 5000 },
    GOLD: { label: "Vàng", color: "#ffd700", icon: "fa-crown", next: null, nextAt: null },
};

const formatDate = (d) => {
    if (!d) return "";
    const date = new Date(d);
    return date.toLocaleDateString("vi-VN") + " " + date.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" });
};

const Loyalty = () => {
    const { t } = useTranslation();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            try {
                const res = await authApis().get(endpoints["loyalty"]);
                setData(res.data);
            } catch (e) {
                setData({ points: 0, tier: "BRONZE", history: [] });
            } finally {
                setLoading(false);
            }
        };
        load();
    }, []);

    if (loading) {
        return (
            <div className="text-center py-5">
                <Spinner animation="border" variant="primary" />
            </div>
        );
    }

    const points = data?.points ?? 0;
    const tier = data?.tier ?? "BRONZE";
    const history = data?.history ?? [];
    const info = tierInfo[tier] || tierInfo.BRONZE;

    return (
        <div className="py-4">
            <h2 className="mb-4">Hạng thành viên</h2>

            <Row className="g-4">
                <Col md={4}>
                    <Card className="shadow-sm border-0 h-100">
                        <Card.Body className="text-center py-4">
                            <div
                                className="rounded-circle d-inline-flex align-items-center justify-content-center mb-3"
                                style={{ width: 80, height: 80, background: info.color, color: "#fff" }}
                            >
                                <i className={`fas ${info.icon} fa-2x`}></i>
                            </div>
                            <Card.Title className="h5">{info.label}</Card.Title>
                            <p className="text-muted small mb-0">Hạng {tier}</p>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card className="shadow-sm border-0 h-100">
                        <Card.Body className="text-center py-4">
                            <div className="display-4 fw-bold text-primary">{points}</div>
                            <p className="text-muted mb-0">Điểm hiện có</p>
                            <small className="text-muted d-block">
                                1 điểm / 10.000 VND khi thanh toán thành công.
                            </small>
                            <small className="text-muted d-block">
                                Có thể dùng điểm để giảm tối đa 20% giá trị đơn hàng (1 điểm = 1.000 VND).
                            </small>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card className="shadow-sm border-0 h-100">
                        <Card.Body className="py-4">
                            {info.next ? (
                                <>
                                    <p className="mb-1 fw-semibold">Hạng tiếp theo: {info.next}</p>
                                    <p className="text-muted small mb-0">Cần {info.nextAt - points} điểm nữa (tổng {info.nextAt} điểm)</p>
                                    <div className="progress mt-2" style={{ height: 8 }}>
                                        <div
                                            className="progress-bar"
                                            role="progressbar"
                                            style={{ width: Math.min(100, (points / info.nextAt) * 100) + "%" }}
                                        />
                                    </div>
                                </>
                            ) : (
                                <p className="text-success mb-0">Bạn đã đạt hạng cao nhất.</p>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            <Card className="mt-4 shadow-sm border-0">
                <Card.Header className="bg-white">
                    <h5 className="mb-0">Lịch sử tích điểm</h5>
                </Card.Header>
                <Card.Body>
                    {history.length === 0 ? (
                        <p className="text-muted mb-0">Chưa có giao dịch tích điểm. Đặt phòng và thanh toán thành công để nhận điểm.</p>
                    ) : (
                        <Table responsive>
                            <thead>
                                <tr>
                                    <th>Thời gian</th>
                                    <th>Điểm</th>
                                    <th>Nội dung</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                {history.map((row, i) => (
                                    <tr key={i}>
                                        <td>{formatDate(row.createdAt)}</td>
                                        <td className="text-success">+{row.points}</td>
                                        <td>{row.reason || t("loyalty.reasonDefault")}</td>
                                        <td>
                                            {row.reservationId && (
                                                <Link to={`/reservations/${row.reservationId}`} className="small">Chi tiết</Link>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    )}
                </Card.Body>
            </Card>

            <div className="mt-3 text-center">
                <Link to="/booking" className="btn btn-primary">Đặt phòng để tích điểm</Link>
            </div>
        </div>
    );
};

export default Loyalty;
