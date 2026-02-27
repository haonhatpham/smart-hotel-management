import { useContext, useEffect, useState } from "react";
import { Button, Container, Nav, Navbar, NavDropdown, Dropdown } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useTranslation } from "react-i18next";
import Apis, { endpoints } from "../../configs/Api";
import { MyUserContext } from "../../configs/MyContexts";
import { getServiceImage, formatPrice } from "../../utils/serviceHelpers";

const truncate = (str, max) => {
    if (!str) return "";
    return str.length <= max ? str : str.slice(0, max) + "…";
};

const Header = () => {
    const { t, i18n } = useTranslation();
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
                        {t("common.hotel")}
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="basic-navbar-nav" />
                    <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        <Link className="nav-link" to="/">{t("nav.home")}</Link>
                      
                        <NavDropdown title={t("nav.roomTypes")} id="room-types-dropdown">
                            {roomTypes.map(roomType => (<Link key={roomType.id} to={`/?typeId=${roomType.id}`} className="dropdown-item">
                            {roomType.name} </Link>))}
                        </NavDropdown>

                        <Dropdown as={Nav.Item} align="end" className="d-flex align-items-center">
                            <Dropdown.Toggle as={Nav.Link} id="services-dropdown-toggle" className="text-dark text-decoration-none nav-link" variant="link">
                                {t("nav.services")}
                            </Dropdown.Toggle>
                            <Dropdown.Menu className="shadow-sm" style={{ minWidth: "320px", padding: "0.5rem 0" }}>
                                {services.map(service => (
                                    <Dropdown.Item key={service.id} as={Link} to={`/services/${service.id}`} className="py-2 px-3">
                                        <div className="d-flex align-items-center gap-3">
                                            <img src={getServiceImage(service)} alt="" className="rounded" style={{ width: 56, height: 56, objectFit: "cover" }} />
                                            <div className="flex-grow-1 min-w-0">
                                                <div className="fw-semibold text-dark">{service.name}</div>
                                                <small className="text-muted d-block">{truncate(service.description || "", 45)}</small>
                                                <span className="text-primary small fw-bold">{formatPrice(service.price)}</span>
                                            </div>
                                        </div>
                                    </Dropdown.Item>
                                ))}
                                <Dropdown.Divider />
                                <Dropdown.Item as={Link} to="/services" className="text-center text-primary small">
                                    {t("nav.viewAllServices")}
                                </Dropdown.Item>
                            </Dropdown.Menu>
                        </Dropdown>
                        
                        <Link className="nav-link" to="/about">{t("nav.about")}</Link>
                        <Link className="nav-link" to="/contact">{t("nav.contact")}</Link>
                        <Dropdown as={Nav.Item} align="end" className="d-flex align-items-center">
                            <Dropdown.Toggle as={Nav.Link} id="lang-dropdown-toggle" className="text-decoration-none nav-link py-2" variant="link">
                                {i18n.language === "vi" ? "VI" : "EN"}
                            </Dropdown.Toggle>
                            <Dropdown.Menu>
                                <Dropdown.Item onClick={() => i18n.changeLanguage("vi")}>{t("nav.langVi")}</Dropdown.Item>
                                <Dropdown.Item onClick={() => i18n.changeLanguage("en")}>{t("nav.langEn")}</Dropdown.Item>
                            </Dropdown.Menu>
                        </Dropdown>
                        {user===null?<>
                            <Link className="nav-link text-success" to="/register">{t("nav.register")}</Link>
                            <Link className="nav-link text-danger" to="/login">{t("nav.login")}</Link>
                        </>:<>
                            <div className="d-flex align-items-center">
                                <img src={user.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.username || 'U')}&background=0D8ABC&color=fff&size=64`} alt="avatar" className="rounded-circle me-2" style={{width: "32px", height: "32px", objectFit: "cover"}} />
                                <Link className="nav-link text-success" to="/profile">{t("nav.hello")} {user.username}</Link>
                                <Link className="nav-link" to="/loyalty">{t("nav.loyalty")}</Link>
                                <Button variant="danger" className="ms-2" onClick={() => dispatch({"type": "logout"})}>{t("nav.logout")}</Button>
                            </div>
                        </>}
                                                    
                        <Button 
                            as={Link}
                            to="/booking"
                            variant="success" 
                            className="ms-2"
                        >
                            <i className="fas fa-calendar-check me-2"></i>
                            {t("nav.booking")}
                        </Button>
                        
                    </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>
        </>
    );
};

export default Header;