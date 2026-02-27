import React from "react";
import { useLocation, Link } from "react-router-dom";
import { useTranslation } from "react-i18next";

const Thankyou = () => {
  const { t } = useTranslation();
  const location = useLocation();
  const params = new URLSearchParams(location.search);
  const state = location.state || {};
  // Ưu tiên query (VNPay/MoMo redirect), không có thì dùng state (Checkout navigate)
  const success = params.get("success") ?? (state.success != null ? String(state.success) : null);
  const method = params.get("method") || state.method;
  const orderId = params.get("orderId") || state.orderId;
  const amount = params.get("amount") != null ? params.get("amount") : state.amount;
  const reservationId = params.get("reservationId") || state.reservationId;

  const isSuccess = success === "true" || success === null; 

  return (
    <div className="py-5 text-center">
      <div className={`mb-4 ${isSuccess ? "text-success" : "text-danger"}`}>
        <i className={`fa-solid ${isSuccess ? "fa-circle-check" : "fa-circle-xmark"}`} style={{ fontSize: 64 }}></i>
        <h3 className="mt-3">{isSuccess ? t("thankyou.successTitle") : t("thankyou.failureTitle")}</h3>
        <p className="text-muted mb-0">
          {isSuccess 
            ? t("thankyou.successText")
            : t("thankyou.failureText")
          }
        </p>
      </div>

      <div className="d-inline-block text-start bg-light border rounded p-3 mb-4" style={{ minWidth: 320 }}>
        <h6 className="mb-3">{t("thankyou.transactionInfo")}</h6>
        {orderId && (
          <div className="d-flex justify-content-between"><span>{t("thankyou.paymentCode")}</span><strong>{orderId}</strong></div>
        )}
        {reservationId && (
          <div className="d-flex justify-content-between"><span>{t("thankyou.reservationCode")}</span><strong>{reservationId}</strong></div>
        )}
        {method && (
          <div className="d-flex justify-content-between"><span>{t("thankyou.method")}</span><strong>{method}</strong></div>
        )}
        {amount && (
          <div className="d-flex justify-content-between"><span>{t("thankyou.amount")}</span><strong>{amount}</strong></div>
        )}
        {!orderId && !reservationId && !method && !amount && (
          <div className="text-muted">{t("thankyou.noData")}</div>
        )}
      </div>

      <div className="d-flex gap-2 justify-content-center">
        {isSuccess && reservationId && (
          <Link to={`/reservations/${reservationId}`} className="btn btn-primary">
            {t("thankyou.viewReservation")}
          </Link>
        )}
        {!isSuccess && (
          <Link to="/booking" className="btn btn-primary">
            {t("thankyou.retryBooking")}
          </Link>
        )}
        <Link to="/" className="btn btn-outline-secondary">{t("thankyou.backHome")}</Link>
        <Link to="/booking" className="btn btn-outline-secondary">
          {isSuccess ? t("thankyou.continueBooking") : t("thankyou.newBooking")}
        </Link>
      </div>
    </div>
  );
};

export default Thankyou;
