const AIPromptLog = require('../models/AIPromptLog');
const AISession = require('../models/AISession'); 

// LƯU LỊCH SỬ CHAT MỚI
exports.saveLog = async (req, res) => {
    try {
        const { 
            user_id, session_id, title, input_prompt, 
            input_image_url, gemini_raw_response, weather_context 
        } = req.body;

        // Validation cơ bản
        if (!user_id || !session_id || !input_prompt) {
            return res.status(400).json({ message: "Thiếu thông tin bắt buộc (user_id, session_id, input_prompt)" });
        }

        // Xử lý Phiên hội thoại (AISession)
        await AISession.findOneAndUpdate(
            { session_id: session_id },
            { 
                user_id: user_id,
                $setOnInsert: { title: title || input_prompt.substring(0, 30) + '...' },
                $currentDate: { updated_at: true } 
            },
            { upsert: true, new: true }
        );

        // Lưu tin nhắn mới vào AIPromptLog
        const newLog = new AIPromptLog({
            session_id,
            input_prompt,
            input_image_url: input_image_url || null,
            gemini_raw_response: gemini_raw_response || null,
            weather_context: weather_context || null
        });

        const savedLog = await newLog.save();
        res.status(201).json({ success: true, data: savedLog });

    } catch (error) {
        console.error("LỖI SAVE AI LOG:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// LẤY DANH SÁCH LỊCH SỬ HỘI THOẠI (Dùng cho Sidebar)
exports.getRecentSessions = async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);
        const sessions = await AISession.find({ user_id: userId })
            .sort({ updated_at: -1 })
            .limit(20);

        res.status(200).json({ success: true, data: sessions });
    } catch (error) {
        console.error("LỖI GET SESSIONS:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// LẤY CHI TIẾT ĐOẠN CHAT CỦA 1 SESSION (Khi click vào 1 item ở Sidebar)
exports.getSessionMessages = async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);
        const sessionId = req.params.sessionId;

        // Bảo mật: Kiểm tra xem Session này có đúng là của user này không
        const sessionOwner = await AISession.findOne({ session_id: sessionId, user_id: userId });
        if (!sessionOwner) {
            return res.status(403).json({ success: false, message: "Không tìm thấy phiên chat hoặc không có quyền truy cập." });
        }

        // Lấy toàn bộ tin nhắn thuộc Session này
        const messages = await AIPromptLog.find({ session_id: sessionId })
            .sort({ created_at: 1 });

        res.status(200).json({ success: true, data: messages });
    } catch (error) {
        console.error("LỖI GET MESSAGES:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};