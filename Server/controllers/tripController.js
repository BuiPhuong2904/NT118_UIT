const Trip = require('../models/Trip');

// 1. Lấy danh sách chuyến đi của người dùng
exports.getTripsByUser = async (req, res) => {
    try {
        const { userId } = req.params;

        const trips = await Trip.find({ user_id: userId })
            .sort({ start_date: -1 });

        return res.status(200).json({
            success: true,
            data: trips
        });

    } catch (err) {
        console.error(err.stack);
        return res.status(500).json({
            success: false,
            message: err.message
        });
    }
};


// 2. Tạo chuyến đi mới
exports.createTrip = async (req, res) => {
    try {
        const {
            user_id,
            destination,
            start_date,
            end_date,
            trip_type,
            transport
        } = req.body;

        // Validate cơ bản
        if (!user_id || !destination) {
            return res.status(400).json({
                success: false,
                message: "Thiếu user_id hoặc điểm đến"
            });
        }

        if (!start_date || !end_date) {
            return res.status(400).json({
                success: false,
                message: "Thiếu ngày bắt đầu hoặc kết thúc"
            });
        }

        if (new Date(start_date) > new Date(end_date)) {
            return res.status(400).json({
                success: false,
                message: "Ngày bắt đầu phải trước ngày kết thúc"
            });
        }

        const newTrip = new Trip({
            user_id,
            destination,
            start_date,
            end_date,
            trip_type,
            transport
        });

        const savedTrip = await newTrip.save();

        return res.status(201).json({
            success: true,
            data: savedTrip
        });

    } catch (err) {
        // 🔥 QUAN TRỌNG: in stack để tìm file lỗi thật
        console.error("FULL ERROR:", err);
        console.error("STACK:", err.stack);

        return res.status(400).json({
            success: false,
            message: "Không thể tạo chuyến đi: " + err.message
        });
    }
};


// 3. Lấy chi tiết 1 chuyến đi
exports.getTripById = async (req, res) => {
    try {
        const trip = await Trip.findOne({ trip_id: req.params.id });

        if (!trip) {
            return res.status(404).json({
                success: false,
                message: "Không tìm thấy chuyến đi"
            });
        }

        return res.status(200).json({
            success: true,
            data: trip
        });

    } catch (err) {
        console.error(err.stack);
        return res.status(500).json({
            success: false,
            message: err.message
        });
    }
};


// 4. Cập nhật chuyến đi
exports.updateTrip = async (req, res) => {
    try {
        const updatedTrip = await Trip.findOneAndUpdate(
            { trip_id: req.params.id },
            req.body,
            { new: true }
        );

        if (!updatedTrip) {
            return res.status(404).json({
                success: false,
                message: "Không tìm thấy chuyến đi để cập nhật"
            });
        }

        return res.status(200).json({
            success: true,
            data: updatedTrip
        });

    } catch (err) {
        console.error(err.stack);
        return res.status(400).json({
            success: false,
            message: err.message
        });
    }
};


// 5. Xóa chuyến đi
exports.deleteTrip = async (req, res) => {
    try {
        const result = await Trip.findOneAndDelete({
            trip_id: req.params.id
        });

        if (!result) {
            return res.status(404).json({
                success: false,
                message: "Không tìm thấy để xóa"
            });
        }

        return res.status(200).json({
            success: true,
            message: "Xóa thành công"
        });

    } catch (err) {
        console.error(err.stack);
        return res.status(500).json({
            success: false,
            message: err.message
        });
    }
};
