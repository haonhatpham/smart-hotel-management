import { useEffect, useState } from "react";
import { Button, Card, Col, Container, Row, Spinner } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Apis, { endpoints } from "../configs/Api";
import { formatPrice } from "../utils/serviceHelpers";
import { getServiceDetailContent } from "../configs/serviceDetailContent";

const ServiceDetail = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    const [service, setService] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            if (!id) return;
            try {
                const res = await Apis.get(endpoints["services"] + "/" + id);
                setService(res.data);
            } catch (e) {
                // Fallback: một số backend trả 404 cho GET /services/:id, thử lấy từ danh sách
                try {
                    const listRes = await Apis.get(endpoints["services"]);
                    const list = listRes.data || [];
                    const numId = Number(id);
                    const found = list.find((s) => s.id === numId || s.id === id);
                    setService(found || null);
                } catch (e2) {
                    setService(null);
                }
            } finally {
                setLoading(false);
            }
        };
        load();
    }, [id]);

    if (loading) {
        return (
            <div className="text-center py-5">
                <Spinner animation="border" variant="primary" />
            </div>
        );
    }

    if (!service) {
        return (
            <div className="py-5 text-center">
                <p className="text-muted">Không tìm thấy dịch vụ.</p>
                <Button as={Link} to="/services" variant="outline-primary">Quay lại danh sách dịch vụ</Button>
            </div>
        );
    }

    const content = getServiceDetailContent(service.name);
    const heroImage = content?.heroImage || "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=1600&q=80";
    const tagline = content?.tagline || service.description || t("common.defaultServiceTagline");
    const longDescription = content?.longDescription || service.description || "Dịch vụ chất lượng cao, phục vụ tận tình. Bạn có thể thêm dịch vụ khi đặt phòng.";
    const gallery = content?.gallery || [];
    const highlights = content?.highlights || [];

    return (
        <div className="service-detail-page">
            {/* Hero */}
            <section
                className="text-center text-white py-5 mb-0"
                style={{
                    marginLeft: "calc(-50vw + 50%)",
                    marginRight: "calc(-50vw + 50%)",
                    width: "100vw",
                    background: `linear-gradient(rgba(0,0,0,0.4), rgba(0,0,0,0.4)), url("${heroImage}") center/cover no-repeat`,
                    minHeight: "300px",
                    display: "flex",
                    flexDirection: "column",
                    justifyContent: "center",
                }}
            >
                <div className="py-4">
                    <nav className="mb-3">
                        <Link to="/services" className="text-white text-opacity-75 small text-decoration-none">← Dịch vụ</Link>
                    </nav>
                    <h1 className="display-5 fw-bold mb-2">{service.name}</h1>
                    <p className="lead mb-0 opacity-90" style={{ maxWidth: 560, margin: "0 auto" }}>
                        {tagline}
                    </p>
                </div>
            </section>

            {/* Intro: ảnh + mô tả dài */}
            <section className="py-5">
                <Container>
                    <Row className="align-items-center g-4">
                        <Col lg={5}>
                            <div className="rounded-3 overflow-hidden shadow" style={{ aspectRatio: "4/3", maxHeight: 380 }}>
                                <img
                                    src={heroImage}
                                    alt={service.name}
                                    className="w-100 h-100"
                                    style={{ objectFit: "cover" }}
                                />
                            </div>
                        </Col>
                        <Col lg={7}>
                            <span className="text-primary fw-semibold small text-uppercase">Dịch vụ</span>
                            <h2 className="h3 fw-bold mt-2 mb-3">{service.name}</h2>
                            <p className="text-muted mb-0" style={{ whiteSpace: "pre-line" }}>
                                {longDescription}
                            </p>
                            <div className="mt-4 h4 text-primary">{formatPrice(service.price)}</div>
                            <Button as={Link} to="/booking" variant="success" size="lg" className="mt-2">
                                Đặt phòng và thêm dịch vụ
                            </Button>
                        </Col>
                    </Row>
                </Container>
            </section>

            {/* Gallery ảnh (món ăn / không gian) */}
            {gallery.length > 0 && (
                <section className="py-5 bg-light">
                    <Container>
                        <h2 className="h4 fw-bold text-center mb-4">Hình ảnh</h2>
                        <Row xs={1} sm={2} lg={3} className="g-4">
                            {gallery.map((img, i) => (
                                <Col key={i}>
                                    <div className="rounded-3 overflow-hidden shadow-sm" style={{ aspectRatio: "4/3" }}>
                                        <img
                                            src={img.src}
                                            alt={img.alt}
                                            className="w-100 h-100"
                                            style={{ objectFit: "cover" }}
                                        />
                                    </div>
                                </Col>
                            ))}
                        </Row>
                    </Container>
                </section>
            )}

            {/* Highlights */}
            {highlights.length > 0 && (
                <section className="py-5">
                    <Container>
                        <h2 className="h4 fw-bold text-center mb-4">Điểm nổi bật</h2>
                        <Row className="justify-content-center">
                            <Col md={8} lg={6}>
                                <ul className="list-unstyled mb-0">
                                    {highlights.map((item, i) => (
                                        <li key={i} className="d-flex align-items-start mb-3">
                                            <i className="fas fa-check-circle text-success me-2 mt-1"></i>
                                            <span className="text-muted">{item}</span>
                                        </li>
                                    ))}
                                </ul>
                            </Col>
                        </Row>
                    </Container>
                </section>
            )}

            {/* CTA cuối */}
            <section className="py-5 bg-light">
                <Container>
                    <div className="text-center">
                        <p className="text-muted mb-3">Thêm dịch vụ khi đặt phòng để được phục vụ tốt nhất.</p>
                        <Button as={Link} to="/booking" variant="primary" size="lg">
                            Đặt phòng ngay
                        </Button>
                    </div>
                </Container>
            </section>
        </div>
    );
};

export default ServiceDetail;
