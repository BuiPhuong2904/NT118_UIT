const User = require("../models/User");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const nodemailer = require("nodemailer");

// ================= REGISTER =================
exports.register = async (req, res) => {
  try {
    const { username, email, password, gender } = req.body;

    if (!username || !email || !password) {
      return res.status(400).json({ message: "Thiếu dữ liệu" });
    }

    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res.status(400).json({ message: "Email đã tồn tại" });
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const newUser = new User({
      username,
      email,
      password_hash: hashedPassword,
      gender
    });

    await newUser.save();

    const token = jwt.sign(
      { id: newUser._id, user_id: newUser.user_id },
      process.env.JWT_SECRET,
      { expiresIn: "7d" }
    );

    res.status(201).json({
      message: "Đăng ký thành công",
      token: token, 
      user: {
        user_id: newUser.user_id,
        username: newUser.username,
        email: newUser.email,
        gender: newUser.gender
      }
    });

  } catch (error) {
    res.status(500).json({ message: "Lỗi server" });
  }
};

// ================= LOGIN =================
exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });
    if (!user) {
      return res.status(400).json({ message: "Email không tồn tại" });
    }

    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      return res.status(400).json({ message: "Sai mật khẩu" });
    }

    const token = jwt.sign(
      { id: user._id, user_id: user.user_id },
      process.env.JWT_SECRET,
      { expiresIn: "7d" }
    );

    res.status(200).json({
      message: "Đăng nhập thành công",
      token,
      user: {
        user_id: user.user_id,
        username: user.username,
        email: user.email,
        gender: user.gender
      }
    });

  } catch (error) {
    res.status(500).json({ message: "Lỗi server" });
  }
};

// ================= FORGOT PASSWORD =================
exports.forgotPassword = async (req, res) => {
  try {
    const { email } = req.body;
    const user = await User.findOne({ email });

    if (!user) {
      return res.status(400).json({ message: "Email không tồn tại" });
    }

    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    user.reset_token = otp;
    user.reset_token_expire = Date.now() + 15 * 60 * 1000; 
    await user.save();

    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
      }
    });

    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: email,
      subject: "Mã OTP khôi phục mật khẩu - SmartFashion",
      html: `
        <h3>Yêu cầu đặt lại mật khẩu</h3>
        <p>Mã OTP của bạn là: <b style="font-size: 24px; color: #4CAF50;">${otp}</b></p>
        <p>Mã này sẽ hết hạn sau 15 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.</p>
      `
    });

    res.json({ message: "Mã OTP đặt lại mật khẩu đã được gửi đến email" });

  } catch (error) {
    console.error("Lỗi forgotPassword:", error);
    res.status(500).json({ message: "Lỗi server" });
  }
};

// ================= RESET PASSWORD =================
exports.resetPassword = async (req, res) => {
  try {
    const { email, otp, newPassword } = req.body; 

    const user = await User.findOne({
      email: email,
      reset_token: otp,
      reset_token_expire: { $gt: Date.now() }
    });

    if (!user) {
      return res.status(400).json({ message: "Mã OTP không hợp lệ hoặc đã hết hạn" });
    }

    // Hash mật khẩu mới
    const hashedPassword = await bcrypt.hash(newPassword, 10);
    user.password_hash = hashedPassword;

    // Xoá OTP đi để không dùng lại được nữa
    user.reset_token = null;
    user.reset_token_expire = null;
    await user.save();

    res.json({ message: "Đặt lại mật khẩu thành công" });

  } catch (error) {
    console.error("Lỗi resetPassword:", error);
    res.status(500).json({ message: "Lỗi server" });
  }
};

