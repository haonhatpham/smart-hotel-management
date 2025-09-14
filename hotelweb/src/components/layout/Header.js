import { useContext, useEffect, useState } from "react";
import { Button, Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Api";
import { MyUserContext } from "../../configs/MyContexts";

const Header = () => {
    const [roomTypes, setRoomTypes] = useState([]);
    const [services, setServices] = useState([]);
    const [user, dispatch] = useContext(MyUserContext);

    const loadRoomTypes = async () => {
        try {
            let res = await Apis.get(endpoints['room-types']);
            setRoomTypes(res.data || []);
        } catch (error) {
        }
    };

    const loadServices = async () => {
        try {
            let res = await Apis.get(endpoints['services']);
            setServices(res.data || []);
        } catch (error) {
        }
    };

    useEffect(() => {
        loadRoomTypes();
        loadServices();
    }, []);

    return (
        <>
            <Navbar expand="lg" className="bg-body-tertiary">
                <Container>
                    <Navbar.Brand as={Link} to="/">
                        <i className="fas fa-hotel me-2"></i>
                        Smart Hotel Management
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Link className="nav-link" to="/">Trang chủ</Link>
                      
                        <NavDropdown title="Loại phòng" id="room-types-dropdown">
                            {roomTypes.map(roomType => (<Link key={roomType.id} to={`/?typeId=${roomType.id}`} className="dropdown-item">
                            {roomType.name} </Link>))}
                        </NavDropdown>

                        <NavDropdown title="Dịch vụ" id="services-dropdown">
                            {services.map(service => (<Link key={service.id} to={`/services/${service.id}`} className="dropdown-item" >
                                    {service.name} </Link>))}
                        </NavDropdown>
                        
                        <Link className="nav-link" to="/about">Về chúng tôi</Link>
                        <Link className="nav-link" to="/contact">Liên hệ</Link>
                        {user===null?<>
                            <Link className="nav-link text-success" to="/register">Đăng ký</Link>
                            <Link className="nav-link text-danger" to="/login">Đăng nhập</Link>
                        </>:<>
                            <Link className="nav-link text-success" to="/profile">Chào {user.username}</Link>
                            <Button variant="danger"onClick={() => dispatch({"type": "logout"})}>Đăng xuất</Button>
                        </>}
                                                    
                        <Button 
                            as={Link}
                            to="/booking"
                            variant="success" 
                            className="ms-2"
                        >
                            <i className="fas fa-calendar-check me-2"></i>
                            ĐẶT PHÒNG
                        </Button>
                        
                    </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
};

export default Header;