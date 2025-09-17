    import { useRef, useState } from "react";
    import { Alert, Button, Form, Container, Row, Col, Card, InputGroup } from "react-bootstrap";
    import MySpinner from "./layout/MySpiner";   
    import Apis, { endpoints } from "../configs/Api";
    import { useNavigate } from "react-router-dom";


    const Register = () => {
        const info = [{
            title: "Tên",
            field: "firstName",
            type: "text"
        }, {
            title: "Họ và tên lót",
            field: "lastName",
            type: "text"
        }, {
            title: "Số điện thoại",
            field: "phone",
            type: "tel"
        }, {
            title: "Email",
            field: "email",
            type: "email"
        }, {
            title: "Tên đăng nhập",
            field: "username",
            type: "text"
        }, {
            title: "Mật khẩu",
            field: "password",
            type: "password"
        }, {
            title: "Xác nhận mật khẩu",
            field: "confirm",
            type: "password"
        }];
        const avatar = useRef();

        const [user, setUser] = useState({});
        const [loading, setLoading] = useState(false);
        const [err, setErr] = useState();
        const nav = useNavigate();
        const [showPwd, setShowPwd] = useState(false);
        const [showConfirm, setShowConfirm] = useState(false);

        const validate = () => {
            const requiredFields = ['firstName', 'lastName', 'phone', 'email', 'username', 'password', 'confirm'];
            
            for (let field of requiredFields) {
                if (!user[field] || user[field].trim() === '') {
                    const fieldTitle = info.find(i => i.field === field)?.title;
                    setErr(`${fieldTitle} không được để trống!`);
                    return false;
                }
            }
            
            if (user.firstName.trim().length < 2) {
                setErr("Tên phải có ít nhất 2 ký tự!");
                return false;
            }
            
            if (user.lastName.trim().length < 2) {
                setErr("Họ và tên lót phải có ít nhất 2 ký tự!");
                return false;
            }
            
            // 3. Kiểm tra username
            if (user.username.length < 3) {
                setErr("Tên đăng nhập phải có ít nhất 3 ký tự!");
                return false;
            }
            
            if (user.username.length > 20) {
                setErr("Tên đăng nhập không được quá 20 ký tự!");
                return false;
            }
            
            const usernameRegex = /^[a-zA-Z0-9_]+$/;
            if (!usernameRegex.test(user.username)) {
                setErr("Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới!");
                return false;
            }
            
            // 4. Kiểm tra email
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(user.email)) {
                setErr("Email không hợp lệ!");
                return false;
            }
            
            const cleanPhone = user.phone.replace(/\s/g, '');
            const phoneRegex = /^[0-9]{10,11}$/;
            if (!phoneRegex.test(cleanPhone)) {
                setErr("Số điện thoại phải có 10-11 chữ số!");
                return false;
            }
            
            if (user.password.length < 6) {
                setErr("Mật khẩu phải có ít nhất 6 ký tự!");
                return false;
            }
            
            if (user.password.length > 50) {
                setErr("Mật khẩu không được quá 50 ký tự!");
                return false;
            }
            
            if (user.password !== user.confirm) {
                setErr("Mật khẩu không khớp!");
                return false;
            }
            
            return true;
        }

        const register = async (e) => {
            e.preventDefault();
            if (validate()) {
                try {
                    setLoading(true);

                    let formData = new FormData();
                    for (let key in user) {
                        if (key !== "confirm" && key !== "firstName" && key !== "lastName") {
                            formData.append(key, user[key]);
                        }
                    }

                    formData.append("fullname", `${user.firstName} ${user.lastName}`);

                    if (avatar.current?.files?.[0])
                        formData.append("avatar", avatar.current.files[0]);

                    let res = await Apis.post(endpoints['register'], formData, {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    });
                    if (res.status === 201)
                        nav('/login');
                    else
                        setErr("Hệ thống có lỗi!");
                } catch (ex) {
                    console.error(ex);
                } finally {
                    setLoading(false);
                }
            }
        }

        return (
            <>
                <Container fluid className="py-5" style={{minHeight: "70vh"}}>
                    <Row className="justify-content-center">
                        <Col xs={12} sm={10} md={10} lg={8} xl={7}>
                            <Card className="shadow-lg border-0 rounded-4">
                                <Card.Body className="p-4 p-md-5">
                                    <div className="text-center mb-4">
                                        <h2 className="fw-bold text-success mb-1">Đăng ký người dùng</h2>
                                        <div className="text-muted">Tạo tài khoản để đặt phòng nhanh chóng</div>
                                    </div>

                                    {err && <Alert variant="danger" className="mb-4">{err}</Alert>}

                                    <Form onSubmit={register}>
                                        <Row>
                                            <Col md={6}>
                                                <Form.Group className="mb-3" controlId="firstName">
                                                    <Form.Label className="fw-semibold">Tên</Form.Label>
                                                    <Form.Control size="lg" value={user.firstName} onChange={e => setUser({...user, firstName: e.target.value})} type="text" placeholder="Nhập tên" required />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group className="mb-3" controlId="lastName">
                                                    <Form.Label className="fw-semibold">Họ và tên lót</Form.Label>
                                                    <Form.Control size="lg" value={user.lastName} onChange={e => setUser({...user, lastName: e.target.value})} type="text" placeholder="Nhập họ và tên lót" required />
                                                </Form.Group>
                                            </Col>
                                        </Row>

                                        <Row>
                                            <Col md={6}>
                                                <Form.Group className="mb-3" controlId="phone">
                                                    <Form.Label className="fw-semibold">Số điện thoại</Form.Label>
                                                    <Form.Control size="lg" value={user.phone} onChange={e => setUser({...user, phone: e.target.value})} type="tel" placeholder="Số điện thoại" required />
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group className="mb-3" controlId="email">
                                                    <Form.Label className="fw-semibold">Email</Form.Label>
                                                    <Form.Control size="lg" value={user.email} onChange={e => setUser({...user, email: e.target.value})} type="email" placeholder="Email" required />
                                                </Form.Group>
                                            </Col>
                                        </Row>

                                        <Form.Group className="mb-3" controlId="username">
                                            <Form.Label className="fw-semibold">Tên đăng nhập</Form.Label>
                                            <Form.Control size="lg" value={user.username} onChange={e => setUser({...user, username: e.target.value})} type="text" placeholder="Tên đăng nhập" required />
                                        </Form.Group>

                                        <Row>
                                            <Col md={6}>
                                                <Form.Group className="mb-3" controlId="password">
                                                    <Form.Label className="fw-semibold">Mật khẩu</Form.Label>
                                                    <InputGroup>
                                                        <Form.Control size="lg" value={user.password} onChange={e => setUser({...user, password: e.target.value})} type={showPwd?"text":"password"} placeholder="Mật khẩu" required />
                                                        <Button variant="outline-secondary" onClick={() => setShowPwd(!showPwd)}>{showPwd?"Ẩn":"Hiện"}</Button>
                                                    </InputGroup>
                                                </Form.Group>
                                            </Col>
                                            <Col md={6}>
                                                <Form.Group className="mb-3" controlId="confirm">
                                                    <Form.Label className="fw-semibold">Xác nhận mật khẩu</Form.Label>
                                                    <InputGroup>
                                                        <Form.Control size="lg" value={user.confirm} onChange={e => setUser({...user, confirm: e.target.value})} type={showConfirm?"text":"password"} placeholder="Xác nhận mật khẩu" required />
                                                        <Button variant="outline-secondary" onClick={() => setShowConfirm(!showConfirm)}>{showConfirm?"Ẩn":"Hiện"}</Button>
                                                    </InputGroup>
                                                </Form.Group>
                                            </Col>
                                        </Row>

                                        <Form.Group className="mb-3" controlId="avatar">
                                            <Form.Label className="fw-semibold">Ảnh đại diện</Form.Label>
                                            <Form.Control size="lg" type="file" ref={avatar} accept="image/*" />
                                        </Form.Group>

                                        {loading ? <MySpinner /> : (
                                            <Button variant="success" type="submit" size="lg" className="w-100 mt-2">Đăng ký</Button>
                                        )}
                                    </Form>
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>
                </Container>
            </>
        ); 
    }

    export default Register;