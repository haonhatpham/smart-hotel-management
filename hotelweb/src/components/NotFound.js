import { Button } from "react-bootstrap";
import { Link } from "react-router-dom";

const NotFound = () => {
    return (
        <div className="text-center py-5">
            <h1 className="display-1 text-muted">404</h1>
            <p className="lead">Trang không tồn tại.</p>
            <p className="text-muted small">URL bạn truy cập có thể sai hoặc đã bị xóa.</p>
            <Link to="/">
                <Button variant="primary">Về trang chủ</Button>
            </Link>
        </div>
    );
};

export default NotFound;
