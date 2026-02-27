import { Row, Col, Card, Button, Container } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

const About = () => {
    const { t } = useTranslation();

    const highlights = [
        { icon: "fa-map-marker-alt", titleKey: "about.location", text: "Trung tâm Quận 1, gần các điểm tham quan, mua sắm và giao thông công cộng." },
        { icon: "fa-concierge-bell", titleKey: "about.service", text: "Đội ngũ 24/7 sẵn sàng phục vụ từ đặt phòng, buffet sáng đến đưa đón sân bay." },
        { icon: "fa-spa", titleKey: "about.amenities", text: "Phòng hiện đại, wifi nhanh, bể bơi, spa và không gian làm việc thoải mái." },
        { icon: "fa-award", titleKey: "about.quality", text: "Giải thưởng khách sạn xuất sắc và đánh giá cao từ khách hàng." },
    ];

    const stats = [
        { value: "10+", labelKey: "about.yearsExp" },
        { value: "50+", labelKey: "about.rooms" },
        { value: "50K+", labelKey: "about.guests" },
        { value: "4.8", labelKey: "about.avgRating" },
    ];

    return (
        <div className="about-page">
            {/* Hero - full width */}
            <section
                className="about-hero text-center text-white py-5 mb-0"
                style={{
                    marginLeft: "calc(-50vw + 50%)",
                    marginRight: "calc(-50vw + 50%)",
                    width: "100vw",
                    background: 'linear-gradient(rgba(0,0,0,0.35), rgba(0,0,0,0.35)), url("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1600&q=80") center/cover no-repeat',
                    minHeight: "320px",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "center",
                }}
            >
                <div className="py-4">
                    <h1 className="display-5 fw-bold mb-3">{t("nav.about")}</h1>
                    <p className="lead mb-0 opacity-90" style={{ maxWidth: 560, margin: "0 auto" }}>
                        Trải nghiệm lưu trú đẳng cấp tại trung tâm thành phố
                    </p>
                </div>
            </section>

            {/* Intro - Our Story */}
            <section className="py-5">
                <Container>
                    <Row className="align-items-center g-4">
                        <Col lg={6}>
                            <div
                                className="rounded-3 overflow-hidden shadow"
                                style={{ aspectRatio: "4/3", maxHeight: 400 }}
                            >
                                <img
                                    src="https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80"
                                    alt="Smart Hotel"
                                    className="w-100 h-100"
                                    style={{ objectFit: "cover" }}
                                />
                            </div>
                        </Col>
                        <Col lg={6}>
                            <span className="text-primary fw-semibold small text-uppercase">Câu chuyện của chúng tôi</span>
                            <h2 className="h3 fw-bold mt-2 mb-3">Smart Hotel Management</h2>
                            <p className="text-muted mb-3">
                                Ra đời với mong muốn mang đến không chỉ chỗ nghỉ mà còn là trải nghiệm đáng nhớ cho mỗi khách. 
                                Chúng tôi kết hợp công nghệ quản lý thông minh với sự phục vụ tận tâm, để mọi chi tiết từ đặt phòng, 
                                thanh toán đến dịch vụ bổ sung đều thuận tiện và rõ ràng.
                            </p>
                            <p className="text-muted mb-0">
                                Tọa lạc tại vị trí vàng Quận 1, Smart Hotel là điểm đến lý tưởng cho công tác và du lịch, 
                                với đầy đủ tiện nghi và đội ngũ luôn sẵn sàng hỗ trợ bạn.
                            </p>
                        </Col>
                    </Row>
                </Container>
            </section>

            {/* Why Choose Us */}
            <section className="py-5 bg-light">
                <Container>
                    <div className="text-center mb-5">
                        <h2 className="h3 fw-bold mb-2">Vì sao chọn chúng tôi</h2>
                        <p className="text-muted">Những lý do khiến Smart Hotel trở thành lựa chọn tin cậy</p>
                    </div>
                    <Row xs={1} md={2} lg={4} className="g-4">
                        {highlights.map((item, i) => (
                            <Col key={i}>
                                <Card className="h-100 border-0 shadow-sm">
                                    <Card.Body className="text-center p-4">
                                        <div
                                            className="rounded-circle d-inline-flex align-items-center justify-content-center mb-3"
                                            style={{ width: 56, height: 56, background: "var(--bs-primary)", color: "white" }}
                                        >
                                            <i className={`fas ${item.icon} fa-lg`}></i>
                                        </div>
                                        <Card.Title className="h6 fw-bold">{t(item.titleKey)}</Card.Title>
                                        <Card.Text className="small text-muted mb-0">{item.text}</Card.Text>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                </Container>
            </section>

            {/* Stats */}
            <section
                className="py-5 text-white"
                style={{
                    marginLeft: "calc(-50vw + 50%)",
                    marginRight: "calc(-50vw + 50%)",
                    width: "100vw",
                    background: "linear-gradient(135deg, #0d6efd 0%, #0a58ca 100%)",
                }}
            >
                <Container>
                    <Row className="g-4 text-center">
                        {stats.map((item, i) => (
                            <Col xs={6} md={3} key={i}>
                                <div className="display-6 fw-bold">{item.value}</div>
                                <div className="small opacity-90">{t(item.labelKey)}</div>
                            </Col>
                        ))}
                    </Row>
                </Container>
            </section>

            {/* Mission & CTA */}
            <section className="py-5">
                <Container>
                    <Row className="justify-content-center">
                        <Col lg={8} className="text-center">
                            <h2 className="h4 fw-bold mb-3">Sứ mệnh của chúng tôi</h2>
                            <p className="text-muted mb-4">
                                Mang đến trải nghiệm lưu trú an toàn, thoải mái và đáng nhớ cho mọi khách hàng, 
                                với dịch vụ minh bạch và công nghệ hỗ trợ đặt phòng, thanh toán dễ dàng.
                            </p>
                            <Button as={Link} to="/booking" variant="primary" size="lg" className="px-4">
                                <i className="fas fa-calendar-check me-2"></i>
                                {t("nav.booking")}
                            </Button>
                        </Col>
                    </Row>
                </Container>
            </section>
        </div>
    );
};

export default About;
