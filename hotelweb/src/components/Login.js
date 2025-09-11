import { Alert, Button, Form } from "react-bootstrap";
import MySpinner from "./layout/MySpiner";   
import { useContext, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import Apis, { authApis,endpoints } from "../configs/Api";
import cookie from 'react-cookies'
import { MyUserContext } from "../configs/MyContexts";

const Login = () => {
    const info = [{
        title: "Tên đăng nhập",
        field: "username",
        type: "text"
    }, {
        title: "Mật khẩu",
        field: "password",
        type: "password"
    }];

    const [user, setUser] = useState();
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState();
    const nav = useNavigate();
    const [,dispatch] = useContext(MyUserContext);
    const [q] = useSearchParams();

    const validate = () => {
        if (user.username === '' || user.password === '') {
            setErr("Vui lòng nhập username và password!");
            return false;
        }

        return true;
    }

    const login = async (e) => {
        e.preventDefault();
        if (validate()) {
            let res = await Apis.post(endpoints['login'], {
                ...user
            });
            console.info(res.data)  
            console.log("Token vừa nhận:", res.data.token);
            cookie.save('token', res.data.token);

            const u = await authApis().get(endpoints['profile']);
            console.info(u.data);
            console.log("Header gọi profile:", authApis().defaults.headers);

            dispatch({
                "type": "login",
                "payload": u.data
            });

            let next = q.get('next')
            nav(next === null?"/":next);
        }
       
    }
    return (
        <>
            <h1 className="text-center text-success mt-1">ĐĂNG NHẬP NGƯỜI DÙNG</h1>

            {err && <Alert variant="danger" className="mt-2">{err}</Alert>}

            <Form onSubmit={login}>
                {info.map(i => <Form.Group key={i.field} className="mb-3" controlId={i.field}>
                                <Form.Label>{i.title}</Form.Label>
                                <Form.Control value={user[i.field]} onChange={e => setUser({...user, [i.field]: e.target.value})} type={i.type} placeholder={i.title} required />
                            </Form.Group>)}

                {loading?<MySpinner />:<Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                    <Button variant="success" type="submit">Đăng nhập</Button>
                </Form.Group>}
                
            </Form> 
        </>
        )
}

export default Login