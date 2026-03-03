const User = require("../models/User");
const bcrypt = require("bcryptjs");

exports.register = async (req, res) => {
  try {
    const { username, email, password } = req.body;

    // kiểm tra thiếu dữ liệu
    if (!username || !email || !password) {
      return res.status(400).json({ message: "Thiếu dữ liệu" });
    }

    // kiểm tra email trùng
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: "Email đã tồn tại" });
    }

    // hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = new User({
      username,
      email,
      password_hash: hashedPassword
    });

    await newUser.save();

    res.status(201).json({
      message: "Đăng ký thành công",
      user: newUser
    });

  } catch (error) {
    console.error("LỖI CHI TIẾT:", error);
    res.status(500).json({ message: "Lỗi server" });
  }
};
