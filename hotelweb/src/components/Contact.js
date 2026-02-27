import { Row, Col, Card } from "react-bootstrap";
import { useTranslation } from "react-i18next";

const HOTEL_ADDRESS = "123 Nguyễn Huệ, Quận 1, TP.HCM";
const HOTEL_LAT = "10.7769";
const HOTEL_LNG = "106.7009";

const Contact = () => {
    const { t } = useTranslation();
    const mapEmbedUrl = `https://www.openstreetmap.org/export/embed.html?bbox=${Number(HOTEL_LNG)-0.01},${Number(HOTEL_LAT)-0.005},${Number(HOTEL_LNG)+0.01},${Number(HOTEL_LAT)+0.005}&layer=mapnik&marker=${HOTEL_LAT},${HOTEL_LNG}`;
    const mapsLink = `https://www.google.com/maps/search/?api=1&query=${HOTEL_LAT},${HOTEL_LNG}`;

    return (
        <div className="py-4">
            <h2 className="mb-4">{t("contact.title")}</h2>
            <Row className="g-4 align-items-start">
                <Col md={5} className="d-flex flex-column gap-3">
                    <Card className="shadow-sm flex-shrink-0">
                        <Card.Body>
                            <Card.Title className="h6"><i className="fas fa-map-marker-alt text-primary me-2"></i>{t("contact.address")}</Card.Title>
                            <p className="mb-0">{HOTEL_ADDRESS}</p>
                            <a href={mapsLink} target="_blank" rel="noreferrer" className="small text-primary">
                                {t("contact.directions")} →
                            </a>
                        </Card.Body>
                    </Card>
                    <Card className="shadow-sm flex-shrink-0">
                        <Card.Body>
                            <Card.Title className="h6"><i className="fas fa-phone text-primary me-2"></i>{t("contact.hotline")}</Card.Title>
                            <p className="mb-0">1900 1234</p>
                        </Card.Body>
                    </Card>
                    <Card className="shadow-sm flex-shrink-0">
                        <Card.Body>
                            <Card.Title className="h6"><i className="fas fa-envelope text-primary me-2"></i>{t("contact.email")}</Card.Title>
                            <p className="mb-0">contact@smarthotel.vn</p>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={7}>
                    <Card className="shadow-sm overflow-hidden">
                        <div className="ratio ratio-16x9">
                            <iframe
                                src={mapEmbedUrl}
                                title="Smart Hotel - Bản đồ"
                                style={{ border: 0 }}
                                allowFullScreen
                                loading="lazy"
                                referrerPolicy="no-referrer-when-downgrade"
                            />
                        </div>
                        <Card.Body className="py-2">
                            <small className="text-muted">
                                Bạn có thể xem chỉ đường chi tiết hoặc đặt lịch đến thăm.
                            </small>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </div>
    );
};

export default Contact;
