import { useState, useRef, useEffect } from "react";
import { Button, Form, InputGroup } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import Apis, { endpoints } from "../configs/Api";
import "./ChatWidget.css";

const QUICK_REPLY_KEYS = ["chat.roomPrice", "chat.policyCancel", "chat.servicesBuffet", "chat.howToBook", "chat.contactHotel"];

const ChatWidget = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [messages, setMessages] = useState([
        { role: "bot", text: "Xin chào! Tôi là trợ lý ảo Smart Hotel. Bạn có thể hỏi về loại phòng, giá dịch vụ, chính sách hủy phòng... Hoặc chọn nhanh bên dưới." }
    ]);
    const [input, setInput] = useState("");
    const [loading, setLoading] = useState(false);
    const bottomRef = useRef(null);

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const sendMessage = async (textToSend) => {
        const text = (textToSend ?? input).trim();
        if (!text || loading) return;

        setInput("");
        setMessages(prev => [...prev, { role: "user", text }]);
        setLoading(true);

        try {
            const res = await Apis.post(endpoints["chat"], { message: text });
            const reply = res.data?.reply || "Xin lỗi, tôi không thể trả lời lúc này.";
            const replySource = res.data?.replySource || "rule";
            setMessages(prev => [...prev, { role: "bot", text: reply, replySource }]);
        } catch (err) {
            setMessages(prev => [...prev, { role: "bot", text: "Kết nối lỗi. Vui lòng thử lại sau." }]);
        } finally {
            setLoading(false);
        }
    };

    const send = () => sendMessage(input);
    const sendQuickReply = (label) => sendMessage(label);

    return (
        <>
            <div className={`chat-widget-panel ${open ? "open" : ""}`}>
                <div className="chat-widget-header">
                    <span><i className="fas fa-robot me-2"></i>{t("chat.assistantTitle")}</span>
                    <Button variant="link" className="text-white p-0" onClick={() => setOpen(false)}>
                        <i className="fas fa-times"></i>
                    </Button>
                </div>
                <div className="chat-widget-body">
                    {messages.map((msg, i) => (
                        <div key={i} className={`chat-msg ${msg.role}`}>
                            {msg.role === "bot" && <i className="fas fa-robot chat-avatar"></i>}
                            <div className="chat-bubble-wrapper">
                                <div className="chat-bubble">{msg.text}</div>
                                {msg.role === "bot" && msg.replySource === "llm" && (
                                    <span className="chat-badge-ai" title="Answered by AI">AI</span>
                                )}
                            </div>
                        </div>
                    ))}
                    {loading && (
                        <div className="chat-msg bot">
                            <i className="fas fa-robot chat-avatar"></i>
                            <div className="chat-bubble typing">Đang trả lời...</div>
                        </div>
                    )}
                    <div ref={bottomRef} />
                </div>
                <div className="chat-widget-footer">
                    <div className="chat-quick-replies">
{QUICK_REPLY_KEYS.map((key) => {
                            const label = t(key);
                            return (
                                <button
                                    key={key}
                                    type="button"
                                    className="chat-quick-reply-chip"
                                    onClick={() => sendQuickReply(label)}
                                    disabled={loading}
                                >
                                    {label}
                                </button>
                            );
                        })}
                    </div>
                    <InputGroup>
                        <Form.Control
                            placeholder="Nhập câu hỏi..."
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && send()}
                            disabled={loading}
                        />
                        <Button variant="primary" onClick={send} disabled={loading}>
                            <i className="fas fa-paper-plane"></i>
                        </Button>
                    </InputGroup>
                </div>
            </div>
            <button
                className="chat-widget-toggle"
                onClick={() => setOpen(!open)}
                aria-label={t("chat.openLabel")}
            >
                <i className={`fas ${open ? "fa-times" : "fa-comments"}`}></i>
            </button>
        </>
    );
};

export default ChatWidget;
