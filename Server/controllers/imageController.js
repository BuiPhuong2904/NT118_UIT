const cloudinary = require('cloudinary').v2;
const axios = require('axios');
const FormData = require('form-data');
const Image = require('../models/Image');

// Khởi tạo Cloudinary bằng Key trong file .env
cloudinary.config({
    cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
    api_key: process.env.CLOUDINARY_API_KEY,
    api_secret: process.env.CLOUDINARY_API_SECRET
});

exports.uploadAndProcessImage = async (req, res) => {
    try {
        if (!req.file) return res.status(400).json({ message: "Không tìm thấy file ảnh" });

        const userId = parseInt(req.body.user_id) || 1; 

        // GỌI API REMOVE.BG (Gửi file trực tiếp qua Buffer)
        const formData = new FormData();
        formData.append('size', 'auto');
        formData.append('image_file', req.file.buffer, { filename: 'upload.jpg' }); // req.file.buffer là ảnh gốc

        const removeBgResponse = await axios.post('https://api.remove.bg/v1.0/removebg', formData, {
            headers: {
                ...formData.getHeaders(),
                'X-Api-Key': process.env.REMOVE_BG_API_KEY
            },
            responseType: 'arraybuffer' // Yêu cầu trả về dạng file (nhị phân)
        });
        const noBgBuffer = Buffer.from(removeBgResponse.data);

    // UPLOAD CẢ 2 ẢNH LÊN CLOUDINARY CÙNG MỘT LÚC
        const [originalResult, noBgResult] = await Promise.all([
            new Promise((resolve, reject) => {
                cloudinary.uploader.upload_stream({ folder: "smart_fashion/original" }, (error, result) => {
                    if (error) reject(error); else resolve(result);
                }).end(req.file.buffer);
            }),
            new Promise((resolve, reject) => {
                cloudinary.uploader.upload_stream({ folder: "smart_fashion/no_bg" }, (error, result) => {
                    if (error) reject(error); else resolve(result);
                }).end(noBgBuffer);
            })
        ]);

        // LƯU VÀO DATABASE BẰNG MODEL
        const newImage = new Image({
            user_id: userId,
            url_original: originalResult.secure_url,
            url_no_bg: noBgResult.secure_url,
            storage_type: 'cloudinary'
        });

        const savedImage = await newImage.save();

        // TRẢ KẾT QUẢ VỀ CHO ANDROID
        res.status(200).json({
            success: true,
            message: "Xử lý ảnh thành công",
            data: savedImage
        });

    } catch (error) {
        console.error("Lỗi xử lý ảnh:", error);
        res.status(500).json({ message: "Lỗi server khi xử lý ảnh", error: error.message });
    }
};