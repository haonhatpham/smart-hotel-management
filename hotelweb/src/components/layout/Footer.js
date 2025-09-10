import { Container, Row, Col } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Footer = () => {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="bg-dark text-light mt-5 pt-4 pb-3">
            <Container>
                <Row>
                    <Col md={4}>
                        <h5 className="text-primary mb-3">
                            <i className="fas fa-hotel me-2"></i>
                            Smart Hotel Management
                        </h5>
                        <p className="text-muted">
                            Hệ thống quản lý khách sạn thông minh với dịch vụ chất lượng cao, 
                            mang đến trải nghiệm lưu trú tuyệt vời.
                        </p>
                    </Col>
                    
                    <Col md={4}>
                        <h6 className="mb-3 text-light">Liên kết nhanh</h6>
                        <ul className="list-unstyled">
                            <li><Link to="/" className="text-light text-decoration-none">Trang chủ</Link></li>
                            <li><Link to="/rooms" className="text-light text-decoration-none">Phòng</Link></li>
                            <li><Link to="/services" className="text-light text-decoration-none">Dịch vụ</Link></li>
                            <li><Link to="/about" className="text-light text-decoration-none">Về chúng tôi</Link></li>
                            <li><Link to="/contact" className="text-light text-decoration-none">Liên hệ</Link></li>
                        </ul>
                    </Col>
                    
                    <Col md={4}>
                        <h6 className="mb-3 text-light">Liên hệ</h6>
                        <p className="text-light mb-1">
                            <i className="fas fa-map-marker-alt me-2"></i>
                            123 Đường ABC, Quận 1, TP.HCM
                        </p>
                        <p className="text-light mb-1">
                            <i className="fas fa-phone me-2"></i>
                            (+84) 123 456 789
                        </p>
                        <p className="text-light mb-1">
                            <i className="fas fa-envelope me-2"></i>
                            info@smarthotel.com
                        </p>
                    </Col>
                </Row>
                
                <hr className="my-3" />
                
                <Row>
                    <Col className="text-center">
                        <p className="text-light mb-0">
                            &copy; {currentYear} Smart Hotel Management. Tất cả quyền được bảo lưu.
                        </p>
                    </Col>
                </Row>
            </Container>
        </footer>
    );
};

export default Footer;
