const Trip = require('../models/Trip');

// ================= 1. LẤY TRIP CỦA USER (/me) =================
exports.getMyTrips = async (req, res) => {
    try {
        const userId = req.user.user_id; // 🔥 lấy từ token

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


// ================= 2. TẠO TRIP =================
exports.createTrip = async (req, res) => {
    try {
        const {
            destination,
            start_date,
            end_date,
            trip_type,
            transport
        } = req.body;

        const user_id = req.user.user_id; // 🔥 KHÔNG lấy từ client nữa

        // Validate
        if (!destination) {
            return res.status(400).json({
                success: false,
                message: "Thiếu điểm đến"
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
            user_id, // 🔥 từ token
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
        console.error("FULL ERROR:", err);
        console.error("STACK:", err.stack);

        return res.status(400).json({
            success: false,
            message: "Không thể tạo chuyến đi: " + err.message
        });
    }
};


// ================= 3. CHI TIẾT TRIP (CÓ CHECK USER) =================
exports.getTripById = async (req, res) => {
    try {
        const userId = req.user.user_id;
        const tripId = req.params.id;

        const trip = await Trip.findOne({
            trip_id: tripId,
            user_id: userId // 🔥 chặn xem của người khác
        });

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


// ================= 4. UPDATE TRIP (CÓ CHECK USER) =================
exports.updateTrip = async (req, res) => {
    try {
        const userId = req.user.user_id;

        const updatedTrip = await Trip.findOneAndUpdate(
            {
                trip_id: req.params.id,
                user_id: userId // 🔥 chỉ update trip của mình
            },
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


// ================= 5. DELETE TRIP (CÓ CHECK USER) =================
exports.deleteTrip = async (req, res) => {
    try {
        const userId = req.user.user_id;

        const result = await Trip.findOneAndDelete({
            trip_id: req.params.id,
            user_id: userId // 🔥 chỉ xoá của mình
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
