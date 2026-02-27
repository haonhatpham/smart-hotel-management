import { Alert, Button, Form, Container, Row, Col, Card, InputGroup } from "react-bootstrap";
import MySpinner from "./layout/MySpiner";
import { useContext, useEffect, useRef, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Apis, { authApis, endpoints } from "../configs/Api";
import cookie from "react-cookies";
import { MyUserContext } from "../configs/MyContexts";

const Login = () => {
    const { t } = useTranslation();
    const info = [
        { titleKey: "login.username", field: "username", type: "text" },
        { titleKey: "login.password", field: "password", type: "password" },
    ];

    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState();
    const nav = useNavigate();
    const [, dispatch] = useContext(MyUserContext);
    const [q] = useSearchParams();
    const googleBtnRef = useRef(null);
    const [showPwd, setShowPwd] = useState(false);

    const validate = () => {
        if (user.username === "" || user.password === "") {
            setErr(t("login.requiredFields"));
            return false;
        }
        return true;
    };

    const handleGoogleCredential = async (credential) => {
        try {
            const res = await Apis.post(endpoints.loginGoogle, { token: credential });
            const token = res.data.token;
            cookie.save("token", token, { path: "/", maxAge: 86400, sameSite: "lax", secure: false });
            const u = await authApis().get(endpoints.profile);
            dispatch({ type: "login", payload: u.data });
            const next = q.get("next");
            nav(next === null ? "/" : next);
        } catch (e) {
            const serverMsg = e?.response?.data?.message ?? e?.message ?? "";
            setErr(serverMsg ? `${t("common.hotel")}: ${serverMsg}` : t("login.wrongCredentials"));
        }
    };

    useEffect(() => {
        const init = async () => {
            try {
                const res = await Apis.get(endpoints.googleClientId);
                const client_id = res.data.client_id;
                if (window.google && googleBtnRef.current && client_id) {
                    window.google.accounts.id.initialize({
                        client_id,
                        callback: (response) => handleGoogleCredential(response.credential),
                    });
                    window.google.accounts.id.renderButton(googleBtnRef.current, { theme: "outline", size: "large", width: 300 });
                }
            } catch (_) {}
        };
        init();
    }, []);
    const login = async (e) => {
        e.preventDefault();
        if (validate()) {
            try {
                setLoading(true);
                setErr(undefined);
                let res = await Apis.post(endpoints['login'], {
                    ...user
                });
                cookie.save('token', res.data.token);

                const u = await authApis().get(endpoints['profile']);

                dispatch({
                    "type": "login",
                    "payload": u.data
                });

                try {
                    const guestCart = cookie.load('cart_guest');
                    if (guestCart) {
                        const userCartKey = `cart_${u.data.id}`;
                        cookie.save(userCartKey, guestCart);
                        cookie.remove('cart_guest');
                    }
                } catch (e) {}

                let next = q.get('next')
                nav(next === null?"/":next);
            } catch (e) {
                const status = e?.response?.status;
                if (status === 401) {
                    setErr(t("login.wrongCredentials"));
                } else {
                    const serverMsg = e?.response?.data?.message ?? e?.message ?? "";
                    setErr(serverMsg ? `${t("login.title")}: ${serverMsg}` : t("login.wrongCredentials"));
                }
            } finally {
                setLoading(false);
            }
        }
    }
    return (
        <>
            <Container fluid className="py-5" style={{minHeight: "70vh"}}>
                <Row className="justify-content-center">
                    <Col xs={12} sm={10} md={8} lg={6} xl={5}>
                        <Card className="shadow-lg border-0 rounded-4">
                            <Card.Body className="p-4 p-md-5">
                                <div className="text-center mb-4">
                                    <h2 className="fw-bold text-success mb-1">{t("login.title")}</h2>
                                    <div className="text-muted">{t("login.welcome")}</div>
                                </div>

                                {err && <Alert variant="danger" className="mb-4">{err}</Alert>}

                                <Form onSubmit={login}>
                                    <Form.Group className="mb-3" controlId="username">
                                        <Form.Label className="fw-semibold">{t("login.username")}</Form.Label>
                                        <Form.Control size="lg" value={user.username || ""} onChange={e => setUser({ ...user, username: e.target.value })} type="text" placeholder={t("login.placeholderUsername")} required />
                                    </Form.Group>

                                    <Form.Group className="mb-3" controlId="password">
                                        <Form.Label className="fw-semibold">{t("login.password")}</Form.Label>
                                        <InputGroup>
                                            <Form.Control size="lg" value={user.password || ""} onChange={e => setUser({ ...user, password: e.target.value })} type={showPwd ? "text" : "password"} placeholder={t("login.placeholderPassword")} required />
                                            <Button variant="outline-secondary" onClick={() => setShowPwd(!showPwd)}>{showPwd ? t("login.hidePwd") : t("login.showPwd")}</Button>
                                        </InputGroup>
                                        <div className="text-end mt-1">
                                            <Link to="/forgot-password" className="small">{t("login.forgotPassword")}</Link>
                                        </div>
                                    </Form.Group>

                                    {loading ? <MySpinner /> : (
                                        <Button variant="success" type="submit" size="lg" className="w-100 mt-2">{t("login.submit")}</Button>
                                    )}

                                    <div className="d-flex align-items-center my-4">
                                        <div className="flex-grow-1"><hr /></div>
                                        <div className="px-2 text-muted">{t("login.or")}</div>
                                        <div className="flex-grow-1"><hr /></div>
                                    </div>

                                    <div className="d-flex justify-content-center">
                                        <div ref={googleBtnRef} />
                                    </div>
                                </Form>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </>
    )
}

export default Login