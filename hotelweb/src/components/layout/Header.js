import { useEffect, useState } from "react";
import { Container, Nav, Navbar, NavDropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Api";

const Header = () => {
    const [roomTypes, setRoomTypes] = useState([]);

    const loadRoomTypes = async () => {
        try {
            let res = await Apis.get(endpoints['room-types']);
            setRoomTypes(res.data || []);
        } catch (error) {
            // Ignore error, just use empty array
        }
    };

    useEffect(() => {
        loadRoomTypes();
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
                            {roomTypes.map(roomType => (
                                <Link 
                                    key={roomType.id}
                                    to={`/?typeId=${roomType.id}`} 
                                    className="dropdown-item"
                                >
                                    {roomType.name}
                                </Link>
                            ))}
                        </NavDropdown>

                        <Link className="nav-link" to="/services">Dịch vụ</Link>
                        <Link className="nav-link" to="/about">Về chúng tôi</Link>
                        <Link className="nav-link" to="/contact">Liên hệ</Link>

                        <Link className="nav-link text-success" to="/register">Đăng ký</Link>
                        <Link className="nav-link text-danger" to="/login">Đăng nhập</Link>
                        
                    </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
};

export default Header;
