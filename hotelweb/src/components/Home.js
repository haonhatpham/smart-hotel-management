import { useContext, useEffect, useState } from "react";
import { Alert, Button, Card, Col, Row, Spinner } from "react-bootstrap";
import Apis, { endpoints } from "../configs/Api";
import { useSearchParams, Link } from "react-router-dom";
import cookie from 'react-cookies';
import { MyCartContext } from "../configs/MyContexts";

const Home = () => {
    const [rooms, setRooms] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(true);
    const [hasMore, setHasMore] = useState(true); 
    const [q] = useSearchParams();

    const loadRooms = async () => {
        try {
            setLoading(true);
            let url = `${endpoints['rooms']}?page=${page}`;

            let typeId = q.get("typeId");
            if (typeId) 
                url = `${url}&roomTypeId=${typeId}`;
          
            let res = await Apis.get(url);
            
            if (res.data && res.data.length > 0) {
                if (page === 1) {
                    setRooms(res.data);
                } else {
                    setRooms([...rooms, ...res.data]);
                }
                
                if (res.data.length < 6) { 
                    setHasMore(false);
                } else {
                    setHasMore(true);
                }
            } else {
                setHasMore(false);
            }
        } catch (ex) {
            console.error("Full error:", ex);
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {        
        if (page > 0) {
            loadRooms();
        }
    }, [page, q]);

    useEffect(() => {
        setPage(1);
        setHasMore(true);
    }, [q]);

    const loadMore = () => {
        setPage(page + 1);
    }

    return (
        <>
            <div className="hero-section text-center py-5 mb-4" style={{
                background: 'linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url("https://images.unsplash.com/photo-1566073771259-6a8506099945?ixlib=rb-4.0.3&auto=format&fit=crop&w=2070&p=80")',
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                color: 'white'
            }}>
                <h1 className="display-4 fw-bold">Smart Hotel Management</h1>
                <p className="lead">Trải nghiệm lưu trú tuyệt vời với dịch vụ chất lượng cao</p>
                <Button 
                    as={Link}
                    to="/booking"
                    variant="primary" 
                    size="lg"
                >
                    Đặt phòng ngay
                </Button>
            </div>

            <div className="container">
                <h2 className="text-center mb-4">Phòng nổi bật</h2>
                
                {rooms.length === 0 && !loading && <Alert variant="warning" className="mt-2">KHÔNG có phòng nào!</Alert>}
                
                <Row>
                    {rooms.map(room => <Col className="p-2" md={4} xs={12} key={room.id}>
                        <Card className="h-100 shadow-sm">
                            <Card.Img 
                                variant="top" 
                                src={room.imageUrl || "https://images.unsplash.com/photo-1611892440504-42a792e24d32?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&p=80"} 
                                style={{height: '200px', objectFit: 'cover'}}
                            />
                            <Card.Body className="d-flex flex-column">
                                <Card.Title>
                                    Phòng {room.roomNumber} - {room.roomTypeId?.name}
                                </Card.Title>
                                <Card.Text className="flex-grow-1">
                                    {room.roomTypeId?.description || room.note || "Phòng thoải mái với đầy đủ tiện nghi hiện đại"}
                                </Card.Text>
                                <div className="mb-3">
                                    <span className="badge bg-success me-2">{room.status}</span>
                                    <span className="text-primary fw-bold fs-5">
                                        {room.roomTypeId?.price?.toLocaleString()} VNĐ/đêm
                                    </span>
                                </div>
                                <div className="d-grid gap-2 d-md-flex">
                                    <Button 
                                        as={Link} 
                                        to={`/rooms/${room.id}`} 
                                        variant="outline-primary" 
                                        className="me-md-2"
                                    >
                                        Xem chi tiết
                                    </Button>
                                    <Button 
                                        variant="primary" 
                                        // onClick={}
                                    >
                                        Thêm vào giỏ
                                    </Button>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>)}
                </Row>

                {loading && (
                    <div className="text-center my-4">
                        <Spinner animation="border" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </Spinner>
                    </div>
                )}

                {hasMore && !loading && (
                    <div className="text-center mt-4 mb-4">
                        <Button variant="info" onClick={loadMore}>Xem thêm phòng...</Button>
                    </div>
                )}
            </div>
        </>
    );
}

export default Home;