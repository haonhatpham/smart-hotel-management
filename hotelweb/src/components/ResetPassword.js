import { useState, useEffect } from "react";
import { Alert, Button, Card, Form } from "react-bootstrap";
import { Link, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Apis, { endpoints } from "../configs/Api";

const ResetPassword = () => {
    const { t } = useTranslation();
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token") || "";
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState(null);
    const [variant, setVariant] = useState("info");
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        if (!token) setMessage(t("resetPassword.missingToken"));
    }, [token, t]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!token) return;
        if (newPassword.length < 6) {
            setMessage(t("resetPassword.minLength"));
            setVariant("danger");
            return;
        }
        if (newPassword !== confirmPassword) {
            setMessage(t("resetPassword.mismatch"));
            setVariant("danger");
            return;
        }
        setLoading(true);
        setMessage(null);
        try {
            const res = await Apis.post(endpoints.resetPassword, { token, newPassword });
            setMessage(res.data?.message || t("resetPassword.successDefault"));
            setVariant("success");
            setSuccess(true);
        } catch (err) {
            setMessage(err?.response?.data?.message || t("resetPassword.failDefault"));
            setVariant("danger");
        } finally {
            setLoading(false);
        }
    };

    if (!token) {
        return (
            <div className="container py-5" style={{ maxWidth: 420 }}>
                <Card>
                    <Card.Body>
                        <Alert variant="warning">{message}</Alert>
                        <Link to="/forgot-password"><Button variant="outline-primary">{t("resetPassword.requestAgain")}</Button></Link>
                        <Link to="/login" className="ms-2"><Button variant="outline-secondary">{t("nav.login")}</Button></Link>
                    </Card.Body>
                </Card>
            </div>
        );
    }

    return (
        <div className="container py-5" style={{ maxWidth: 420 }}>
            <Card>
                <Card.Body>
                    <h5 className="mb-3">{t("resetPassword.title")}</h5>
                    {success ? (
                        <>
                            <Alert variant="success">{message}</Alert>
                            <Link to="/login"><Button variant="primary">{t("nav.login")}</Button></Link>
                        </>
                    ) : (
                        <Form onSubmit={handleSubmit}>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("resetPassword.newPasswordLabel")}</Form.Label>
                                <Form.Control
                                    type="password"
                                    value={newPassword}
                                    onChange={(e) => setNewPassword(e.target.value)}
                                    disabled={loading}
                                />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>{t("resetPassword.confirmLabel")}</Form.Label>
                                <Form.Control
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    disabled={loading}
                                />
                            </Form.Group>
                            {message && <Alert variant={variant}>{message}</Alert>}
                            <Button type="submit" variant="primary" disabled={loading}>
                                {loading ? t("resetPassword.processing") : t("resetPassword.submit")}
                            </Button>
                            <Link to="/login" className="ms-2"><Button variant="outline-secondary">{t("resetPassword.cancel")}</Button></Link>
                        </Form>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
};

export default ResetPassword;
