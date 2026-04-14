const Schedule = require('../models/Schedule');
const Outfit = require('../models/Outfit');

// 1. Thêm lịch trình mới
exports.createSchedule = async (req, res) => {
    try {
        const { user_id, outfit_id, date, event_name, event_type, location, weather_note } = req.body;

        if (!user_id || !outfit_id || !date) {
            return res.status(400).json({ success: false, message: 'Thiếu thông tin bắt buộc (user_id, outfit_id, date)' });
        }

        const newSchedule = new Schedule({
            user_id,
            outfit_id,
            date: new Date(date), // Chuyển chuỗi ngày từ Android thành Date object
            event_name,
            event_type,
            location,
            weather_note
        });

        const savedSchedule = await newSchedule.save();
        res.status(201).json({ success: true, data: savedSchedule });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

// 2. Lấy danh sách các ngày CÓ LỊCH TRONG THÁNG (để chấm chấm đỏ trên Lịch)
exports.getPlannedDaysInMonth = async (req, res) => {
    try {
        const { userId } = req.params;
        const { year, month } = req.query; // Ví dụ: year=2026, month=2

        if (!year || !month) {
            return res.status(400).json({ success: false, message: 'Vui lòng cung cấp year và month' });
        }

        // Tạo khoảng thời gian từ đầu tháng đến cuối tháng
        const startDate = new Date(year, month - 1, 1);
        const endDate = new Date(year, month, 0, 23, 59, 59);

        const schedules = await Schedule.find({
            user_id: userId,
            date: { $gte: startDate, $lte: endDate }
        }).select('date').lean();

        // Lọc ra danh sách các ngày (ví dụ: [5, 12, 15, 20])
        const plannedDays = [...new Set(schedules.map(s => {
            const d = new Date(s.date);
            return d.getDate();
        }))];

        res.status(200).json({ success: true, data: plannedDays });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

// 3. Lấy chi tiết lịch trình CỦA 1 NGÀY CỤ THỂ
exports.getSchedulesByDate = async (req, res) => {
    try {
        const { userId } = req.params;
        const { date } = req.query; 

        if (!date) {
            return res.status(400).json({ success: false, message: 'Vui lòng cung cấp ngày (date)' });
        }

        const startDate = new Date(`${date}T00:00:00.000Z`);
        const endDate = new Date(`${date}T23:59:59.999Z`);

        const schedules = await Schedule.aggregate([
            {
                $match: {
                    user_id: parseInt(userId),
                    date: { $gte: startDate, $lte: endDate }
                }
            },
            {
                $lookup: {
                    from: 'outfits',
                    localField: 'outfit_id',
                    foreignField: 'outfit_id',
                    as: 'outfit_data'
                }
            },
            {
                $unwind: {
                    path: '$outfit_data',
                    preserveNullAndEmptyArrays: true
                }
            },
            {
                $addFields: {
                    outfit_id: '$outfit_data' 
                }
            },
            {
                $project: {
                    outfit_data: 0 
                }
            },
            {
                $sort: { date: 1 } 
            }
        ]);

        res.status(200).json({ success: true, data: schedules });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

// 4. Xóa lịch trình
exports.deleteSchedule = async (req, res) => {
    try {
        await Schedule.findOneAndDelete({ schedule_id: parseInt(req.params.id) });
        res.status(200).json({ success: true });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

// 5. Cập nhật chi tiết lịch trình
exports.updateSchedule = async (req, res) => {
    try {
        const scheduleId = parseInt(req.params.id);
        const { event_name, location } = req.body;

        const updatedSchedule = await Schedule.findOneAndUpdate(
            { schedule_id: scheduleId }, 
            { 
                $set: { 
                    event_name: event_name, 
                    location: location 
                } 
            },
            { new: true } 
        );

        if (!updatedSchedule) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy lịch trình để cập nhật' });
        }

        res.status(200).json({ success: true, data: updatedSchedule });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};