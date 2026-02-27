import { useEffect, useState } from "react";
import { Card, Form, Button, Alert, Spinner } from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { authApis, endpoints } from "../configs/Api";

const ReviewForm = () => {
  const { t } = useTranslation();
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [existing, setExisting] = useState(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    authApis().get(endpoints.review(id))
      .then(res => {
        if (res.status === 200) setExisting(res.data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [id]);

  const submit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const payload = { rating: Number(rating), comment };
      const res = await authApis().post(endpoints.review(id), payload);
      if (res.status === 201) navigate("/profile");
    } catch (err) {
      setError(err?.response?.data || t("review.submitError"));
    }
  };

  if (loading) return <div className="text-center py-5"><Spinner animation="border" /></div>;
  if (existing) return (
    <div className="container my-4" style={{ maxWidth: 720 }}>
      <Card>
        <Card.Body>
          <h5 className="mb-3">{t("review.alreadySubmitted")} #{id}</h5>
          <div className="mb-2"><strong>{t("review.rating")}:</strong> {existing.rating} / 5</div>
          {existing.comment && (
            <div className="mb-3"><strong>{t("review.comment")}:</strong><div className="mt-1">{existing.comment}</div></div>
          )}
          <Alert variant="info" className="mb-3">{t("review.alreadySubmittedNote")}</Alert>
          <Button variant="secondary" onClick={() => navigate("/profile")}>{t("profile.backToProfile")}</Button>
        </Card.Body>
      </Card>
    </div>
  );

  return (
    <div className="container my-4" style={{ maxWidth: 720 }}>
      <Card>
        <Card.Body>
          <h5 className="mb-3">{t("review.title")} #{id}</h5>
          {error && <Alert variant="danger">{error}</Alert>}
          <Form onSubmit={submit}>
            <Form.Group className="mb-3">
              <Form.Label>{t("review.rating")}</Form.Label>
              <Form.Select value={rating} onChange={e => setRating(e.target.value)}>
                {[5, 4, 3, 2, 1].map(n => <option key={n} value={n}>{n} ★</option>)}
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>{t("review.comment")}</Form.Label>
              <Form.Control as="textarea" rows={5} value={comment} onChange={e => setComment(e.target.value)} placeholder={t("review.commentPlaceholder")} />
            </Form.Group>
            <div className="d-flex gap-2">
              <Button type="submit" variant="success">{t("review.send")}</Button>
              <Button variant="outline-secondary" onClick={() => navigate(-1)}>{t("review.cancel")}</Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </div>
  );
};

export default ReviewForm;


