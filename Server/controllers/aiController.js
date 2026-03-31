const { GoogleGenerativeAI } = require('@google/generative-ai');
const axios = require('axios');

// Khởi tạo Gemini API với Key
const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

exports.analyzeClothing = async (req, res) => {
    try {
        const { imageUrl } = req.body;
        if (!imageUrl) return res.status(400).json({ message: "Thiếu link ảnh (imageUrl)" });

        console.log("Đang phân tích ảnh:", imageUrl);

        // Tải ảnh từ URL về bộ nhớ đệm (Buffer) để gửi cho Gemini
        const imageResp = await axios.get(imageUrl, { responseType: 'arraybuffer' });
        const mimeType = imageResp.headers['content-type'];
        
        const imageParts = [
            {
                inlineData: {
                    data: Buffer.from(imageResp.data).toString("base64"),
                    mimeType
                }
            }
        ];

        // Gọi model Gemini 1.5 Flash (Model chuyên xử lý ảnh cực nhanh và chính xác của Google)
        const model = genAI.getGenerativeModel({ model: "gemini-3-flash-preview" });

        // Ra lệnh (Prompt) cho AI
        const prompt = `Bạn là một chuyên gia thời trang. Hãy phân tích món đồ trong ảnh và trả về kết quả dưới dạng JSON. 
        Cấu trúc JSON bắt buộc:
        {
            "name": "Tên món đồ (VD: Áo thun nam cổ tròn, Chân váy chữ A...)",
            "category_name": "Tên phân loại chi tiết (BẮT BUỘC CHỌN 1: Áo thun, Áo sơ mi, Áo Polo, Áo len, Áo kiểu, Quần Jeans, Quần Âu, Quần Kaki, Quần Short, Quần Legging, Chân váy Chữ A, Chân váy Bút chì, Đầm Mini, Đầm Midi, Đầm Maxi, Blazer/Vest, Cardigan, Hoodie, Áo khoác Jean, Áo khoác gió, Áo khoác Đại hàn, Giày thể thao, Giày cao gót, Giày bệt, Giày tây, Boots, Túi xách tay, Túi đeo chéo, Balo, Ví (Wallets))",
            "color_hex": "Mã màu Hex đại diện (VD: #000000)",
            "color_family": "Nhóm màu cơ bản (BẮT BUỘC CHỌN 1: Black, White, Gray, Beige, Brown, Red, Orange, Yellow, Green, Blue, Purple, Pink, Multicolor)",
            "material": "Chất liệu vải (VD: Cotton, Len, Kaki, Denim, Lụa, Da, Voan, Nỉ...)",
            "seasons": ["Tên các mùa phù hợp, chọn từ: Mùa Xuân, Mùa Hạ, Mùa Thu, Mùa Đông, Bốn Mùa"],
            "weathers": ["Tên thời tiết phù hợp, chọn từ: Nắng Nóng, Mát Mẻ, Lạnh, Mưa"],
            "occasions": ["Tên dịp phù hợp, chọn từ: Đi Làm, Đi Học, Đi Chơi, Tiệc Tùng, Thể Thao, Mặc Nhà"],
            "styles": ["Phong cách phù hợp, chọn từ: Cơ Bản, Thanh Lịch, Năng Động, Nữ Tính, Cá Tính, Vintage"]
        }
        Lưu ý: Chỉ chọn các giá trị có trong danh sách gợi ý cho phần mảng và nhóm màu.`;

        // Gửi yêu cầu và đợi kết quả
        const result = await model.generateContent([prompt, ...imageParts]);
        const responseText = result.response.text();
        
        // Làm sạch kết quả
        const cleanJsonString = responseText.replace(/```json/g, '').replace(/```/g, '').trim();
        const analyzedData = JSON.parse(cleanJsonString);

        res.status(200).json({ success: true, data: analyzedData });

    } catch (error) {
        console.error("Lỗi khi AI phân tích:", error);
        res.status(500).json({ success: false, message: "Lỗi AI phân tích", error: error.message });
    }
};