    import { useRef, useState } from "react";
    import { Alert, Button, Form } from "react-bootstrap";
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

                    if (avatar)
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
                <h1 className="text-center text-success mt-1">ĐĂNG KÝ NGƯỜI DÙNG</h1>

                {err && <Alert variant="danger" className="mt-2">{err}</Alert>}

                <Form onSubmit={register}>
                    {info.map(i => <Form.Group key={i.field} className="mb-3" controlId={i.field}>
                                    <Form.Label>{i.title}</Form.Label>
                                    <Form.Control value={user[i.field]} onChange={e => setUser({...user, [i.field]: e.target.value})} type={i.type} placeholder={i.title} required />
                                </Form.Group>)}

                    <Form.Group className="mb-3" controlId="avatar">
                        <Form.Label>Ảnh đại diện</Form.Label>
                        <Form.Control type="file" ref={avatar} />
                    </Form.Group>

                    {loading?<MySpinner />:<Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                        <Button variant="success" type="submit">Đăng ký</Button>
                    </Form.Group>}
                    
                </Form>
            </>
        ); 
    }

    export default Register;