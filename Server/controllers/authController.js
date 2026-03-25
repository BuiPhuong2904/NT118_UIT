const User = require("../models/User");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const crypto = require("crypto");
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

    res.status(201).json({
      message: "Đăng ký thành công",
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
      return res.status(400).json({
        message: "Email không tồn tại"
      });
    }

    // tạo token
    const token = crypto.randomBytes(32).toString("hex");

    user.reset_token = token;
    user.reset_token_expire = Date.now() + 3600000; // 1 giờ

    await user.save();

    // tạo transporter gửi mail
    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
      }
    });

    const resetLink = `https://fxkj9c98-3000.asse.devtunnels.ms/reset-password.html?token=${token}`;


    // gửi mail
    await transporter.sendMail({
      from: process.env.EMAIL_USER,
      to: email,
      subject: "Reset mật khẩu SmartFashion",
      html: `
        <h3>Yêu cầu đặt lại mật khẩu</h3>
        <p>Nhấn vào link dưới để đặt lại mật khẩu:</p>
        <a href="${resetLink}">${resetLink}</a>
        <p>Link này sẽ hết hạn sau 1 giờ.</p>
      `
    });

    res.json({
      message: "Email đặt lại mật khẩu đã được gửi",
      token : token
    });

  } catch (error) {
    res.status(500).json({
      message: "Lỗi server"
    });
  }
};
// ================= RESET PASSWORD =================
exports.resetPassword = async (req, res) => {
  try {

    const { token, newPassword } = req.body;

    const user = await User.findOne({
      reset_token: token,
      reset_token_expire: { $gt: Date.now() }
    });

    if (!user) {
      return res.status(400).json({
        message: "Token không hợp lệ hoặc đã hết hạn"
      });
    }

    // hash mật khẩu mới
    const hashedPassword = await bcrypt.hash(newPassword, 10);

    user.password_hash = hashedPassword;

    // xoá token
    user.reset_token = undefined;
    user.reset_token_expire = undefined;

    await user.save();

    res.json({
      message: "Đặt lại mật khẩu thành công"
    });

  } catch (error) {
    res.status(500).json({
      message: "Lỗi server"
    });
  }
};
// ================= GET USER BY TOKEN =================
exports.getUserByToken = async (req, res) => {
  try {
    const { token } = req.query;

    const user = await User.findOne({
      reset_token: token,
      reset_token_expire: { $gt: Date.now() }
    });

    if (!user) {
      return res.status(400).json({
        message: "Token không hợp lệ hoặc đã hết hạn"
      });
    }

    res.json({
      username: user.username
    });

  } catch (error) {
    res.status(500).json({ message: "Lỗi server" });
  }
};

// ================= RESET PASSWORD PAGE =================
exports.resetPasswordPage = (req, res) => {

  const token = req.params.token;

  res.send(`
    <h2>Reset Password</h2>
    <form action="/api/auth/reset-password" method="POST">
      <input type="hidden" name="token" value="${token}" />
      
      <label>New Password:</label><br>
      <input type="password" name="newPassword" required />
      <br><br>

      <button type="submit">Reset Password</button>
    </form>
  `);

};
