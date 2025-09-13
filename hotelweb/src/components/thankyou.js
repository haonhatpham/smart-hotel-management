import React from "react";
import { useLocation, Link } from "react-router-dom";

const Thankyou = () => {
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const method = params.get("method");
  const orderId = params.get("orderId");
  const amount = params.get("amount");
  const reservationId = params.get("reservationId");

  return (
    <div className="py-5 text-center">
      <div className="mb-4 text-success">
        <i className="fa-solid fa-circle-check" style={{ fontSize: 64 }}></i>
        <h3 className="mt-3">Thanh toán thành công</h3>
        <p className="text-muted mb-0">Cảm ơn bạn đã đặt phòng tại khách sạn của chúng tôi.</p>
      </div>

      <div className="d-inline-block text-start bg-light border rounded p-3 mb-4" style={{ minWidth: 320 }}>
        <h6 className="mb-3">Thông tin giao dịch</h6>
        {orderId && (
          <div className="d-flex justify-content-between"><span>Mã thanh toán:</span><strong>{orderId}</strong></div>
        )}
        {reservationId && (
          <div className="d-flex justify-content-between"><span>Mã đặt phòng:</span><strong>{reservationId}</strong></div>
        )}
        {method && (
          <div className="d-flex justify-content-between"><span>Phương thức:</span><strong>{method}</strong></div>
        )}
        {amount && (
          <div className="d-flex justify-content-between"><span>Số tiền:</span><strong>{amount}</strong></div>
        )}
        {!orderId && !reservationId && !method && !amount && (
          <div className="text-muted">Không có dữ liệu giao dịch. Bạn có thể quay lại trang chủ.</div>
        )}
      </div>

      <div className="d-flex gap-2 justify-content-center">
        {reservationId && (
          <Link to={`/reservations/${reservationId}`} className="btn btn-primary">
            Xem đặt phòng
          </Link>
        )}
        <Link to="/" className="btn btn-outline-secondary">Về trang chủ</Link>
        <Link to="/booking" className="btn btn-outline-secondary">Tiếp tục đặt phòng</Link>
      </div>
    </div>
  );
};

export default Thankyou;
