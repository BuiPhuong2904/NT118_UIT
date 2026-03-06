const User = require("../models/User");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");

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
