// Ảnh placeholder theo tên dịch vụ (dùng chung cho Header dropdown, trang Services, ServiceDetail)
export const getServiceImage = (service) => {
    const name = (service?.name || "").toLowerCase();
    if (name.includes("breakfast") || name.includes("buffet")) return "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400&h=300&fit=crop";
    if (name.includes("airport") || name.includes("transfer")) return "https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?w=400&h=300&fit=crop";
    if (name.includes("spa")) return "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=400&h=300&fit=crop";
    return "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=400&h=300&fit=crop";
};

export const formatPrice = (price) => {
    if (price == null) return "";
    return new Intl.NumberFormat("vi-VN").format(Number(price)) + " VND";
};
