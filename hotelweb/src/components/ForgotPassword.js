import { useState } from "react";
import { Alert, Button, Card, Form } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Apis, { endpoints } from "../configs/Api";

const ForgotPassword = () => {
    const { t } = useTranslation();
    const [email, setEmail] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);
    const [variant, setVariant] = useState("info");

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!email.trim()) {
            setMessage(t("forgotPassword.requiredEmail"));
            setVariant("danger");
            return;
        }
        setLoading(true);
        setMessage(null);
        try {
            const res = await Apis.post(endpoints.forgotPassword, { email: email.trim() });
            setMessage(res.data?.message || t("forgotPassword.successDefault"));
            setVariant("success");
        } catch (err) {
            setMessage(err?.response?.data?.message || t("forgotPassword.failDefault"));
            setVariant("danger");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container py-5" style={{ maxWidth: 420 }}>
            <Card>
                <Card.Body>
                    <h5 className="mb-3">{t("forgotPassword.title")}</h5>
                    <p className="text-muted small">{t("forgotPassword.description")}</p>
                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>{t("forgotPassword.emailLabel")}</Form.Label>
                            <Form.Control
                                type="email"
                                placeholder="email@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={loading}
                            />
                        </Form.Group>
                        {message && <Alert variant={variant}>{message}</Alert>}
                        <div className="d-flex gap-2">
                            <Button type="submit" variant="primary" disabled={loading}>
                                {loading ? t("forgotPassword.sending") : t("forgotPassword.submit")}
                            </Button>
                            <Link to="/login">
                                <Button variant="outline-secondary">{t("forgotPassword.backToLogin")}</Button>
                            </Link>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </div>
    );
};

export default ForgotPassword;
