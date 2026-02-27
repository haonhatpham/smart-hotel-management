import { useEffect, useState } from "react";
import { Card, Col, Row, Spinner } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Apis, { endpoints } from "../configs/Api";
import { getServiceImage, formatPrice } from "../utils/serviceHelpers";

const Services = () => {
    const { t } = useTranslation();
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const load = async () => {
            try {
                const res = await Apis.get(endpoints["services"]);
                setServices(res.data || []);
            } catch (e) {
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

    return (
        <div className="py-4">
            <h2 className="mb-4">{t("services.title")}</h2>
            <p className="text-muted mb-4">{t("services.subtitle")}</p>
            <Row xs={1} md={2} lg={3} className="g-4">
                {services.map((service) => (
                    <Col key={service.id}>
                        <Card as={Link} to={`/services/${service.id}`} className="h-100 text-decoration-none text-dark shadow-sm border-0 overflow-hidden" style={{ transition: "transform 0.2s, box-shadow 0.2s" }}>
                            <div className="bg-light" style={{ height: 200, overflow: "hidden" }}>
                                <img
                                    src={getServiceImage(service)}
                                    alt={service.name}
                                    className="w-100 h-100"
                                    style={{ objectFit: "cover" }}
                                />
                            </div>
                            <Card.Body>
                                <Card.Title className="h5">{service.name}</Card.Title>
                                <Card.Text className="text-muted small">
                                    {service.description || "Dịch vụ chất lượng cao."}
                                </Card.Text>
                                <div className="text-primary fw-bold">{formatPrice(service.price)}</div>
                                <span className="small text-primary">{t("services.viewDetail")} →</span>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </div>
    );
};

export default Services;
