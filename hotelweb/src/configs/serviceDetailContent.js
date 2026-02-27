/**
 * Nội dung mở rộng cho từng trang chi tiết dịch vụ (ảnh, mô tả dài, gallery).
 * Khớp theo tên dịch vụ từ API (name).
 */
export const serviceDetailContent = {
    Breakfast: {
        heroImage: "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=1600&q=80",
        tagline: "Bữa sáng buffet đa dạng, tươi ngon mỗi ngày",
        longDescription: "Bắt đầu ngày mới với buffet sáng phong phú tại Smart Hotel. Chúng tôi phục vụ từ 6h00 – 10h00 với đầy đủ món Á – Âu: bánh mì, bơ, mứt, ngũ cốc, trứng chế biến theo yêu cầu, mì phở nóng, salad tươi, trái cây theo mùa và đồ uống không giới hạn. Không gian thoáng đãng, view đẹp, phù hợp cho bữa sáng gia đình hoặc họp nhẹ trước giờ làm.",
        gallery: [
            { src: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80", alt: "Salad và đồ tươi" },
            { src: "https://images.unsplash.com/photo-1551218808-94e220e084d2?w=600&q=80", alt: "Bánh ngọt và bánh mì" },
            { src: "https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?w=600&q=80", alt: "Đồ uống và trái cây" },
        ],
        highlights: [
            "Buffet tự chọn, phục vụ 6h00 – 10h00",
            "Món Á – Âu, trứng chế biến theo yêu cầu",
            "Trái cây tươi, bánh ngọt, đồ uống không giới hạn",
            "Không gian thoáng, view đẹp",
        ],
    },
    "Airport Transfer": {
        heroImage: "https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?w=1600&q=80",
        tagline: "Đưa đón sân bay an toàn, đúng giờ",
        longDescription: "Dịch vụ đưa đón sân bay giúp bạn không lo về phương tiện khi đến hoặc rời TP.HCM. Xe riêng, lái xe chuyên nghiệp, theo dõi chuyến bay để điều chỉnh giờ đón phù hợp. Phù hợp khách đoàn hoặc gia đình có hành lý nhiều. Bạn có thể đặt kèm khi đặt phòng hoặc bổ sung sau.",
        gallery: [
            { src: "https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?w=600&q=80", alt: "Xe đón khách" },
            { src: "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80", alt: "Sân bay" },
        ],
        highlights: [
            "Xe riêng, lái xe chuyên nghiệp",
            "Theo dõi chuyến bay, đón đúng giờ",
            "Phù hợp đoàn hoặc gia đình",
            "Đặt kèm khi đặt phòng hoặc bổ sung sau",
        ],
    },
    "Spa 60m": {
        heroImage: "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=1600&q=80",
        tagline: "60 phút thư giãn với massage và chăm sóc da",
        longDescription: "Gói Spa 60 phút tại Smart Hotel mang đến giây phút thư giãn sau hành trình dài. Bao gồm massage toàn thân với tinh dầu thiên nhiên và chăm sóc da mặt cơ bản. Không gian yên tĩnh, nhân viên được đào tạo bài bản. Đặt trước qua lễ tân hoặc thêm vào đơn đặt phòng để được ưu đãi.",
        gallery: [
            { src: "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=600&q=80", alt: "Không gian spa" },
            { src: "https://images.unsplash.com/photo-1600334129128-685c5582fd35?w=600&q=80", alt: "Massage" },
        ],
        highlights: [
            "60 phút massage toàn thân + chăm sóc da mặt",
            "Tinh dầu thiên nhiên, không gian yên tĩnh",
            "Nhân viên được đào tạo chuyên nghiệp",
            "Đặt trước qua lễ tân hoặc kèm đơn đặt phòng",
        ],
    },
};

/**
 * Lấy nội dung mở rộng theo tên dịch vụ; không có thì trả về null (dùng mô tả mặc định).
 */
export const getServiceDetailContent = (serviceName) => {
    if (!serviceName) return null;
    return serviceDetailContent[serviceName] || null;
};
