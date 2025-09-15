import { useContext, useEffect, useState } from "react";
import { Alert, Button, Card, Col, Row, Spinner, Form } from "react-bootstrap";
import Apis, { authApis, endpoints } from "../configs/Api";
import { useSearchParams, useNavigate } from "react-router-dom";
import cookie from 'react-cookies';
import { MyCartContext, MyUserContext } from "../configs/MyContexts";

const Booking = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const [searchResults, setSearchResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [roomTypes, setRoomTypes] = useState([]);
    const [cartState, cartDispatch] = useContext(MyCartContext);
    const [user] = useContext(MyUserContext);
    const [dateError, setDateError] = useState('');
    const [expandedRoom, setExpandedRoom] = useState(null); 
    const [services, setServices] = useState([]);
    const [, setCustomerProfile] = useState(null);
    const [searchForm, setSearchForm] = useState({
        checkIn: '',
        checkOut: '',
        guests: '2',
        roomType: ''
    });

    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => {
        loadRoomTypes();
        loadServices();
        setDefaultDatesIfEmpty();
    }, []);

    
    // Sync giỏ hàng từ cookie chỉ một lần khi component mount
    useEffect(() => {
        const cartKey = user ? `cart_${user.id}` : 'cart_guest';
        const saved = cookie.load(cartKey) || {};
        const items = Object.values(saved);
        
        // Reset state trước khi sync để tránh duplicate
        cartDispatch({ type: "reset" });
        
        // Thêm lại tất cả items từ cookie
        items.forEach(item => {
            cartDispatch({
                type: "add",
                payload: item
            });
        });
    }, [user]); // Chỉ chạy khi user thay đổi

    useEffect(() => {
        if (searchParams.get('checkIn') && searchParams.get('checkOut')) {
            setSearchForm({
                checkIn: searchParams.get('checkIn'),
                checkOut: searchParams.get('checkOut'),
                guests: searchParams.get('guests') || '2',
                roomType: searchParams.get('roomType') || ''
            });
            searchRooms();
        }
    }, [searchParams]);

    // Không tự động xóa phòng khi thay đổi ngày - để người dùng tự quyết định

    // Tải Customer Profile khi đã đăng nhập (refetch khi user sẵn sàng)
    useEffect(() => {
        const token = cookie.load('token');
        if (!token) return;
        console.log('[Booking] Fetching customer-profile...');
        authApis().get(endpoints['customer-profile'])
            .then(res => {
                console.log('[Booking] customer-profile response:', res.status, res.data);
                setCustomerProfile(res.data);
            })
            .catch(err => {
                console.error('[Booking] customer-profile error:', err);
                setCustomerProfile(null);
            });
    }, [user]);

    const loadRoomTypes = async () => {
        try {
            const res = await Apis.get(endpoints['room-types']);
            setRoomTypes(res.data || []);
        } catch (error) {
            console.error('Lỗi ko load được loại phòng:', error);
        }
    };

    const loadServices = async () => {
        try {
            const res = await Apis.get(endpoints['services']);
            setServices(res.data || []);
        } catch (error) {
            console.error('Lỗi ko load được dịch vụ:', error);
        }
    };

    const setDefaultDatesIfEmpty = () => {
        if (!searchParams.get('checkIn')) {
            const today = new Date();
            const tomorrow = new Date(today);
            tomorrow.setDate(tomorrow.getDate() + 1);
            
            const defaultCheckIn = today.toISOString().split('T')[0];
            const defaultCheckOut = tomorrow.toISOString().split('T')[0];
    
            setSearchForm(prev => ({
                ...prev,
                checkIn: defaultCheckIn,
                checkOut: defaultCheckOut
            }));
    
            // set vào URL -> trigger useEffect([searchParams])
            setSearchParams({
                checkIn: defaultCheckIn,
                checkOut: defaultCheckOut,
                guests: '2'
            });
        } else {
            // nếu URL có sẵn checkIn/checkOut thì search luôn
            searchRooms();
        }
    };

    const searchRooms = async () => {
        try {
            setLoading(true);
            let url = `${endpoints['rooms']}`;
            const params = new URLSearchParams();
            if (searchForm.checkIn && searchForm.checkOut) {
                params.append('checkIn', searchForm.checkIn);
                params.append('checkOut', searchForm.checkOut);
                if (searchForm.guests) 
                    params.append('minCapacity', searchForm.guests);
                if (searchForm.roomType) 
                    params.append('roomTypeId', searchForm.roomType);
            }

            const query = params.toString();
            if (query) url += `?${query}`;

            const res = await Apis.get(url);
            setSearchResults(res.data || []);
        } catch (error) {
            console.error('Search failed:', error);
            setSearchResults([]);
        } finally {
            setLoading(false);
        }
    };

    const validateDates = (checkIn, checkOut) => {
        if (!checkIn || !checkOut) {
            setDateError('');
            return true;
        }

        const checkInDate = new Date(checkIn);
        const checkOutDate = new Date(checkOut);
        
        if (checkInDate >= checkOutDate) {
            setDateError('Ngày trả phòng phải sau ngày nhận phòng');
            return false;
        }
        
        setDateError('');
        return true;
    };

    const handleSearchSubmit = () => {
        if (!searchForm.checkIn || !searchForm.checkOut) {
            alert('Vui lòng chọn ngày nhận và trả phòng');
            return;
        }

        if (!validateDates(searchForm.checkIn, searchForm.checkOut)) {
            return;
        }

        const newParams = {
            checkIn: searchForm.checkIn,
            checkOut: searchForm.checkOut,
            guests: searchForm.guests
        };
                
        if (searchForm.roomType) {
            newParams.roomType = searchForm.roomType;
        }
        
        setSearchParams(newParams);
    };

    const handleSelectRoom = (room) => {
        const existingRoom = cartState.rooms.find(r => r.id === room.id);
        if (existingRoom) {
            handleRemoveRoom(room.id, existingRoom.checkIn, existingRoom.checkOut);
            setExpandedRoom(null);
            return;
        }

        if (expandedRoom === room.id) {
            setExpandedRoom(null);
        } else {
            setExpandedRoom(room.id);
        }
    };

    const handleAddRoomToCart = (room, selectedServices = []) => {        
        const nights = searchForm.checkIn && searchForm.checkOut ? 
            Math.ceil((new Date(searchForm.checkOut) - new Date(searchForm.checkIn)) / (1000 * 60 * 60 * 24)) : 1;
        
        const roomBooking = {
            id: room.id,
            roomNumber: room.roomNumber,
            roomType: room.roomTypeId?.name,
            price: room.roomTypeId?.price, 
            checkIn: searchForm.checkIn,
            checkOut: searchForm.checkOut,
            guests: searchForm.guests,
            nights: nights,
            services: selectedServices.map(service => ({
                id: service.id,
                name: service.name,
                price: service.price,  
                quantity: nights,      
                totalPrice: service.price * nights  
            }))
        };

        const cartKey = user ? `cart_${user.id}` : 'cart_guest';
        let cart = cookie.load(cartKey) || {};
        
        const existingRoom = cartState.rooms.find(r => r.id === room.id);
        if (existingRoom) {
            const oldBookingKey = `${room.id}_${existingRoom.checkIn}_${existingRoom.checkOut}`;
            if (cart[oldBookingKey]) {
                delete cart[oldBookingKey];
            }
            cartDispatch({ 
                type: "delete", 
                payload: { id: room.id, checkIn: existingRoom.checkIn, checkOut: existingRoom.checkOut }
            });
        }
        
        const newBookingKey = `${room.id}_${searchForm.checkIn}_${searchForm.checkOut}`;
        cart[newBookingKey] = roomBooking;
        cookie.save(cartKey, cart);

        cartDispatch({ 
            type: "add", 
            payload: roomBooking
        });

        setExpandedRoom(null);
    };


    const handleRemoveRoom = (roomId, checkIn, checkOut) => {
        const cartKey = user ? `cart_${user.id}` : 'cart_guest';
        let cart = cookie.load(cartKey) || {};
        const bookingKey = `${roomId}_${checkIn}_${checkOut}`;
        
        if (cart[bookingKey]) {
            delete cart[bookingKey];
            cookie.save(cartKey, cart);
        }
        
        cartDispatch({ 
            type: "delete", 
            payload: { id: roomId, checkIn, checkOut }
        });
    };



    const navigate = useNavigate();

    const handleBookNow = () => {
        if (cartState.rooms.length === 0) {
            alert('Vui lòng chọn ít nhất một phòng');
            return;
        }

        if (!user) {
            navigate('/login?next=/checkout');
            return;
        }

        navigate('/checkout');
    };

    return (
        <div className="container my-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>
                    <i className="fas fa-search me-2"></i>
                    Tìm kiếm phòng
                </h2>
            </div>

            <Card className="mb-4">
                <Card.Body>
                    <Row className="g-3">
                        <Col md={3}>
                            <Form.Group>
                                <Form.Label>Ngày nhận phòng</Form.Label>
                                        <Form.Control 
                                            type="date" 
                                            value={searchForm.checkIn}
                                            onChange={(e) => {
                                                const newCheckIn = e.target.value;
                                                setSearchForm(prev => ({...prev, checkIn: newCheckIn}));
                                                validateDates(newCheckIn, searchForm.checkOut);
                                            }}
                                            min={new Date().toISOString().split('T')[0]}
                                            isInvalid={!!dateError}
                                        />
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group>
                                <Form.Label>Ngày trả phòng</Form.Label>
                                        <Form.Control 
                                            type="date" 
                                            value={searchForm.checkOut}
                                            onChange={(e) => {
                                                const newCheckOut = e.target.value;
                                                setSearchForm(prev => ({...prev, checkOut: newCheckOut}));
                                                validateDates(searchForm.checkIn, newCheckOut);
                                            }}
                                            min={searchForm.checkIn || new Date().toISOString().split('T')[0]}
                                            isInvalid={!!dateError}
                                        />
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>Số khách</Form.Label>
                                <Form.Select 
                                    value={searchForm.guests}
                                    onChange={(e) => setSearchForm(prev => ({...prev, guests: e.target.value}))}
                                >
                                    <option value="1">1 khách</option>
                                    <option value="2">2 khách</option>
                                    <option value="3">3 khách</option>
                                    <option value="4">4+ khách</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={2}>
                            <Form.Group>
                                <Form.Label>Loại phòng</Form.Label>
                                <Form.Select 
                                    value={searchForm.roomType}
                                    onChange={(e) => setSearchForm(prev => ({...prev, roomType: e.target.value}))}
                                >
                                    <option value="">Tất cả</option>
                                    {roomTypes.map(type => (
                                        <option key={type.id} value={type.id}>
                                            {type.name}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        <Col md={2} className="d-flex align-items-end">
                            <Button 
                                variant="success" 
                                className="w-100"
                                onClick={handleSearchSubmit}
                                disabled={!searchForm.checkIn || !searchForm.checkOut || !!dateError}
                            >
                                <i className="fas fa-search me-2"></i>
                                Tìm phòng
                            </Button>
                        </Col>
                    </Row>
                    
                    {/* Hiển thị lỗi ngày tháng */}
                    {dateError && (
                        <Row className="mt-2">
                            <Col>
                                <Alert variant="danger" className="mb-0">
                                    <i className="fas fa-exclamation-triangle me-2"></i>
                                    {dateError}
                                </Alert>
                            </Col>
                        </Row>
                    )}
                </Card.Body>
            </Card>

            <Row>
                <Col md={8}>
                    {loading ? (
                        <div className="text-center py-5">
                            <Spinner animation="border" />
                            <p className="mt-2">Đang tìm kiếm...</p>
                        </div>
                    ) : searchResults.length === 0 ? (
                        <Alert variant="info" className="text-center py-4">
                            <i className="fas fa-info-circle fa-2x mb-2"></i>
                            <h5>Không tìm thấy phòng trống</h5>
                            <p>Vui lòng thử thay đổi ngày hoặc loại phòng.</p>
                        </Alert>
                    ) : (
                        <div>
                            {searchResults.map(room => {
                                const nights = searchForm.checkIn && searchForm.checkOut ? 
                                    Math.ceil((new Date(searchForm.checkOut) - new Date(searchForm.checkIn)) / (1000 * 60 * 60 * 24)) : 1;
                                const totalPrice = room.roomTypeId?.price * nights;
                                const isSelected = cartState.rooms.some(r => r.id === room.id);
                                
                                return (
                                    <>
                                    <Card 
                                        key={room.id}
                                        className={`${expandedRoom === room.id ? 'mb-0' : 'mb-3'} shadow-sm ${isSelected ? 'border-success' : ''}`}
                                        style={expandedRoom === room.id ? {
                                            borderBottomLeftRadius: 0,
                                            borderBottomRightRadius: 0
                                        } : {}}
                                    >
                                        <Row className="g-0">
                                            <Col md={5}>
                                                <Card.Img src={room.imageUrl} style={{height: '200px', objectFit: 'cover', borderRadius: '8px 0 0 8px'}}/>
                                            </Col>
                                            <Col md={7}>
                                                <Card.Body className="h-100 d-flex flex-column">
                                                    <div className="flex-grow-1">
                                                        <Card.Title className="h5 text-primary">
                                                            {room.roomTypeId?.name}
                                                        </Card.Title>
                                                        <div className="mb-2">
                                                            <i className="fas fa-users me-2"></i>
                                                            <span>{room.roomTypeId?.capacity} khách</span>
                                                            <i className="fas fa-bed ms-3 me-2"></i>
                                                            <span>1 giường đôi</span>
                                                            <i className="fas fa-bath ms-3 me-2"></i>
                                                            <span>1 phòng tắm</span>
                                                        </div>
                                                        <Card.Text className="text-muted small">
                                                            {room.roomTypeId?.description || "Wifi miễn phí • Điều hòa • TV • Minibar • Cà phê/trà miễn phí"}
                                                        </Card.Text>
                                                    </div>
                                                    
                                                    <div className="d-flex justify-content-between align-items-center">
                                                        <div>
                                                            <div className="d-flex align-items-center mb-1">
                                                                <i className="fas fa-check-circle text-success me-2"></i>
                                                                <span className="text-success small">Đặt phòng ngay, thanh toán sau</span>
                                                            </div>
                                                            <div className="d-flex align-items-center">
                                                                <i className="fas fa-times-circle text-danger me-2"></i>
                                                                <span className="text-danger small">Không hoàn tiền</span>
                                                            </div>
                                                        </div>
                                                        
                                                        <div className="text-end">
                                                            <div className="h5 text-primary fw-bold mb-0">
                                                                {totalPrice?.toLocaleString()} VND
                                                            </div>
                                                            <small className="text-muted">
                                                                Chi phí cho {nights} đêm, {searchForm.guests} khách
                                                            </small>
                                                            <div className="mt-2">
                                                                <Button 
                                                                    variant={isSelected ? "danger" : "success"}
                                                                    size="sm"
                                                                    onClick={() => handleSelectRoom(room)}
                                                                >
                                                                    {isSelected ? "Bỏ chọn" : "Chọn phòng"}
                                                                </Button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </Card.Body>
                                            </Col>
                                        </Row>
                                    </Card>

                                    {expandedRoom === room.id && (
                                        <div
                                            className="mb-0"
                                            style={{
                                                borderTop: '2px dashed #dee2e6'
                                            }}
                                        />
                                    )}

                                    {expandedRoom === room.id && (
                                        <Card 
                                            className="mt-0 mb-4 shadow-sm"
                                            style={{
                                                borderTopLeftRadius: 0,
                                                borderTopRightRadius: 0,
                                                borderTop: 'none'
                                            }}
                                        >
                                            
                                            <Card.Body>
                                                <div className="mb-3">
                                                    <small className="text-muted">
                                                        Chọn các dịch vụ bổ sung cho phòng này (tùy chọn)
                                                    </small>
                                                </div>
                                                
                                                {services.length > 0 ? (
                                                    <div className="row g-2">
                                                        {services.map(service => (
                                                            <div key={service.id} className="col-md-6">
                                                                <div className="form-check">
                                                                    <input 
                                                                        className="form-check-input" 
                                                                        type="checkbox" 
                                                                        id={`service-${room.id}-${service.id}`}
                                                                        value={service.id}
                                                                    />
                                                                    <label 
                                                                        className="form-check-label d-flex justify-content-between w-100" 
                                                                        htmlFor={`service-${room.id}-${service.id}`}
                                                                    >
                                                                        <span>{service.name}</span>
                                                                        <span className="text-primary fw-bold">
                                                                            +{service.price?.toLocaleString()} VND
                                                                        </span>
                                                                    </label>
                                                                </div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                ) : (
                                                    <div className="text-center text-muted py-3">
                                                        <i className="fas fa-spinner fa-spin me-2"></i>
                                                        Đang tải dịch vụ...
                                                    </div>
                                                )}
                                                
                                                <div className="mt-3 d-flex gap-2">
                                                    <Button 
                                                        variant="success" 
                                                        size="sm"
                                                        onClick={() => {
                                                            const checkboxes = document.querySelectorAll(`input[type="checkbox"][id^="service-${room.id}-"]:checked`);
                                                            const selectedServices = Array.from(checkboxes).map(cb => {
                                                                const serviceId = parseInt(cb.value);
                                                                return services.find(s => s.id === serviceId);
                                                            }).filter(Boolean);
                                                            
                                                            handleAddRoomToCart(room, selectedServices);
                                                        }}
                                                    >
                                                        <i className="fas fa-plus me-2"></i>
                                                        Thêm vào giỏ
                                                    </Button>
                                                    <Button variant="outline-secondary"  size="sm" onClick={() => setExpandedRoom(null)}>
                                                        <i className="fas fa-times me-2"></i>
                                                        Hủy
                                                    </Button>
                                                </div>
                                            </Card.Body>
                                        </Card>
                                    )}
                                </>
                                );
                            })}
                        </div>
                    )}
                </Col>

                <Col md={4}>
                    <Card className="sticky-top" style={{top: '20px'}}>
                        <Card.Body>
                            {searchForm.checkIn && searchForm.checkOut && (
                                <div className="mb-3 p-3 bg-light rounded">
                                    <div className="fw-bold mb-2">
                                        <i className="fas fa-calendar me-2"></i>
                                        {searchForm.checkIn} → {searchForm.checkOut}
                                    </div>
                                    <div className="text-muted">
                                        {Math.ceil((new Date(searchForm.checkOut) - new Date(searchForm.checkIn)) / (1000 * 60 * 60 * 24))} đêm • {searchForm.guests} khách
                                    </div>
                                </div>
                            )}

                            {cartState.rooms.length === 0 ? (
                                <div className="text-center py-4 text-muted">
                                    <i className="fas fa-bed fa-2x mb-2"></i>
                                    <p>Chưa có phòng nào được chọn</p>
                                </div>
                            ) : (
                                <div>
                                    <h6 className="mb-3">Phòng đã chọn:</h6>
                                    {cartState.rooms.map(room => (
                                        <div key={room.id} className="border rounded p-3 mb-2">
                                            <div className="d-flex justify-content-between align-items-start">
                                                <div className="flex-grow-1">
                                                    <div className="fw-bold">{room.roomType}</div>
                                                    <small className="text-muted">
                                                        {room.guests} khách • {room.nights} đêm
                                                    </small>
                                                    <div className="small text-muted">
                                                        <i className="fas fa-calendar me-1"></i>
                                                        {room.checkIn} → {room.checkOut}
                                                    </div>
                                                    
                                                    {/* Hiển thị dịch vụ đã chọn */}
                                                    {room.services && room.services.length > 0 && (
                                                        <div className="mt-2">
                                                            <small className="text-info fw-bold">Dịch vụ đi kèm:</small>
                                                            <ul className="list-unstyled mt-1">
                                                                {room.services.map(service => (
                                                                    <li key={service.id} className="small text-muted">
                                                                        <i className="fas fa-check-circle text-success me-1"></i>
                                                                        {service.name} x{service.quantity} (+{service.totalPrice?.toLocaleString()} VND)
                                                                    </li>
                                                                ))}
                                                            </ul>
                                                        </div>
                                                    )}
                                                </div>
                                                <div className="text-end">
                                                    <div className="fw-bold text-primary">
                                                        {(room.price * room.nights + (room.services?.reduce((sum, s) => sum + s.totalPrice, 0) || 0)).toLocaleString()} VND
                                                    </div>
                                                    <Button 
                                                        variant="outline-danger" 
                                                        size="sm"
                                                        onClick={() => handleRemoveRoom(room.id, room.checkIn, room.checkOut)}
                                                    >
                                                        <i className="fas fa-trash"></i>
                                                    </Button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                    
                                    <hr />
                                    
                                    <div className="d-flex justify-content-between align-items-center mb-3">
                                        <span className="h5 mb-0">Tổng cộng:</span>
                                        <span className="h4 text-primary fw-bold mb-0">
                                            {cartState.total.toLocaleString()} VND
                                        </span>
                                    </div>
                                    
                                    <div className="text-muted small mb-3">
                                        Bao gồm thuế & phí
                                    </div>
                                    
                                    <Button 
                                        variant={user ? "success" : "warning"}
                                        size="lg" 
                                        className="w-100"
                                        onClick={handleBookNow}
                                    >
                                        <i className="fas fa-credit-card me-2"></i>
                                        {user ? 'Đặt phòng' : 'Bạn cần đăng nhập để thanh toán'}
                                    </Button>
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </div>
    );
};

export default Booking;