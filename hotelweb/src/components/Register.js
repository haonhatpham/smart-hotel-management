    import { use, useRef, useState } from "react";
    import { Alert, Button, Form } from "react-bootstrap";
    import MySpinner from "./layout/MySpiner";   
    import Apis, { authApis,endpoints } from "../configs/Api";
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
            if (user.password == null || user.password != user.confirm) {
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
                        // append các field trừ confirm, firstName, lastName
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