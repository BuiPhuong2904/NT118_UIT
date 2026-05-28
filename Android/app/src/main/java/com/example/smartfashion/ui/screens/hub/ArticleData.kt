package com.example.smartfashion.ui.screens.hub

data class MockArticle(
    val id: String,
    val title: String,
    val category: String,
    val author: String,
    val date: String,
    val readTime: String,
    val imageUrl: String,
    val intro: String,
    val sections: List<MockArticleSection>
)

data class MockArticleSection(
    val heading: String,
    val body: String
)

object MockArticleData {
    val articles = listOf(
        MockArticle(
            id = "article_1",
            title = "5 Cách Phối Đồ Layering 'Chuẩn' Fashionista Cho Mùa Đông",
            category = "TRENDING • MÙA ĐÔNG",
            author = "Bởi Vogue Editor",
            date = "12 Tháng 2, 2026",
            readTime = "5 phút đọc",
            imageUrl = "https://res.cloudinary.com/deyyywbrg/image/upload/Article_01_gyip4c.png",
            intro = "Layering (phối đồ nhiều lớp) không chỉ là cách giữ ấm hiệu quả mà còn là nghệ thuật thể hiện cá tính. Tuy nhiên, nếu không khéo léo, bạn rất dễ biến mình thành một 'chiếc bánh nếp' di động.",
            sections = listOf(
                MockArticleSection("1. Quy tắc mỏng trong - dày ngoài", "Luôn bắt đầu với lớp áo mỏng nhất (như áo giữ nhiệt, sơ mi) và tăng dần độ dày ra bên ngoài (len, áo khoác dạ)."),
                MockArticleSection("2. Chơi đùa với độ dài", "Sự chênh lệch độ dài giữa các lớp áo (ví dụ: áo sơ mi dài hơn áo len tà ngắn) sẽ tạo chiều sâu cho tổng thể trang phục."),
                MockArticleSection("3. Giới hạn 3 gam màu", "Để tránh rối mắt, tổng thể trang phục layering không nên vượt quá 3 gam màu chính. Hãy chọn các màu trung tính làm nền.")
            )
        ),
        MockArticle(
            id = "article_2",
            title = "Bánh Xe Màu Sắc: Bí Quyết Phối Đồ Không Bao Giờ Lỗi Mốt",
            category = "MẸO & TRICKS",
            author = "Bởi Stylist Lyly",
            date = "05 Tháng 3, 2026",
            readTime = "4 phút đọc",
            imageUrl = "https://res.cloudinary.com/deyyywbrg/image/upload/Article_02_ng0kxl.png",
            intro = "Bạn có bao giờ bối rối không biết chiếc áo xanh này nên mặc với quần màu gì? Bánh xe màu sắc chính là 'cứu tinh' của bạn.",
            sections = listOf(
                MockArticleSection("Phối màu tương phản", "Chọn hai màu đối diện nhau trên bánh xe màu sắc (ví dụ: Cam và Xanh dương) để tạo ấn tượng mạnh."),
                MockArticleSection("Phối màu liền kề", "Chọn 3 màu nằm cạnh nhau. Cách này tạo ra sự thanh lịch, hài hòa mà vẫn rất nổi bật.")
            )
        ),
        MockArticle(
            id = "article_3",
            title = "Minimalism: Tối Giản Nhưng Không Hề Đơn Điệu",
            category = "PHONG CÁCH",
            author = "Bởi Fashionista",
            date = "20 Tháng 4, 2026",
            readTime = "4 phút đọc",
            imageUrl = "https://res.cloudinary.com/deyyywbrg/image/upload/Article_03_hthhmz.png",
            intro = "Phong cách tối giản (Minimalism) đang lên ngôi. Chìa khóa không nằm ở việc bạn có ít đồ, mà là việc bạn chọn đồ chất lượng.",
            sections = listOf(
                MockArticleSection("Chất liệu là vua", "Khi thiết kế đơn giản, chất liệu sẽ lên tiếng. Hãy đầu tư vào lanh, lụa, và cotton cao cấp."),
                MockArticleSection("Phom dáng vừa vặn", "Một chiếc áo sơ mi trắng cơ bản nhưng được cắt may tinh tế sẽ đẹp hơn hàng chục chiếc áo cầu kỳ nhưng sai phom.")
            )
        ),
        MockArticle(
            id = "article_4",
            title = "Capsule Wardrobe: Xây Dựng Tủ Đồ Nhỏ Gọn Hoàn Hảo",
            category = "SMART FASHION",
            author = "Bởi Admono",
            date = "10 Tháng 5, 2026",
            readTime = "3 phút đọc",
            imageUrl = "https://res.cloudinary.com/deyyywbrg/image/upload/Article_04_bdpesp.png",
            intro = "Capsule Wardrobe là khái niệm về một tủ đồ chỉ gồm 30-40 món đồ thiết yếu, dễ dàng mix&match với nhau tạo ra hàng trăm outfit.",
            sections = listOf(
                MockArticleSection("Bắt đầu với màu trung tính", "Đen, trắng, xám, beige, và navy nên chiếm 70% tủ đồ của bạn."),
                MockArticleSection("Quy tắc 80/20", "80% là những món đồ cơ bản (basic), 20% còn lại là những món đồ theo trend để làm điểm nhấn.")
            )
        )
    )

    fun getArticleById(id: String): MockArticle? {
        return articles.find { it.id == id }
    }
}