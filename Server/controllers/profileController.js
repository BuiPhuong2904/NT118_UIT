const User = require('../models/User');
const UserProfile = require('../models/UserProfile');

// GET /api/profile/me
const getMyProfile = async (req, res) => {
  try {
    console.log("===== GET PROFILE =====");
    const userId = req.user?.user_id;

    if (!userId) {
      return res.status(401).json({
        success: false,
        message: 'Token không hợp lệ hoặc chưa đăng nhập'
      });
    }

    const user = await User.findOne({ user_id: userId });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'Không tìm thấy người dùng'
      });
    }

    const profile = await UserProfile.findOne({ user_id: userId });

    return res.status(200).json({
      success: true,
      data: {
        user_id: user.user_id,
        username: user.username,
        email: user.email,
        // Sửa: Lấy từ user.avatar_url để khớp Database
        avatarUrl: user.avatar_url || "https://tuanluupiano.com/wp-content/uploads/2026/01/avatar-facebook-mac-dinh-6.jpg", 
        membership: "Thành viên Vàng",
        height: profile?.height || null,
        weight: profile?.weight || null,
        body_shape: profile?.body_shape || "",
        skin_tone: profile?.skin_tone || "",
        style_favourite: profile?.style_favourite || "",
        colors_favourite: profile?.colors_favourite || ""
      }
    });

  } catch (error) {
    console.error("===== LỖI GET PROFILE =====");
    return res.status(500).json({
      success: false,
      message: error.message
    });
  }
};

// PUT /api/profile/me
const updateMyProfile = async (req, res) => {
  try {
    console.log("===== UPDATE PROFILE =====");
    console.log("req.body =", req.body);

    const userId = req.user?.user_id;

    if (!userId) {
      return res.status(401).json({
        success: false,
        message: 'Token không hợp lệ hoặc chưa đăng nhập'
      });
    }

    const {
      username,
      email,
      height,
      weight,
      body_shape,
      skin_tone,
      style_favourite,
      colors_favourite
    } = req.body;

    const userUpdateData = {};
    if (username !== undefined) userUpdateData.username = username;
    if (email !== undefined) userUpdateData.email = email;
    
    // Xử lý file upload từ Android
    if (req.file) {
      const imageUrl = `${req.protocol}://${req.get('host')}/uploads/avatars/${req.file.filename}`;
      // Sửa: Gán vào trường avatar_url cho đúng Schema
      userUpdateData.avatar_url = imageUrl; 
    }

    const updatedUser = await User.findOneAndUpdate(
      { user_id: userId },
      userUpdateData,
      { new: true }
    );

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: 'Không tìm thấy người dùng'
      });
    }

    let profile = await UserProfile.findOne({ user_id: userId });

    if (!profile) {
      profile = new UserProfile({
        user_id: userId
      });
    }

    if (height !== undefined) profile.height = Number(height);
    if (weight !== undefined) profile.weight = Number(weight);
    if (body_shape !== undefined) profile.body_shape = body_shape;
    if (skin_tone !== undefined) profile.skin_tone = skin_tone;
    if (style_favourite !== undefined) profile.style_favourite = style_favourite;
    if (colors_favourite !== undefined) profile.colors_favourite = colors_favourite;

    await profile.save();

    return res.status(200).json({
      success: true,
      message: 'Cập nhật hồ sơ thành công',
      data: {
        user_id: updatedUser.user_id,
        username: updatedUser.username,
        email: updatedUser.email,
        // Trả về avatarUrl khớp với Android
        avatarUrl: updatedUser.avatar_url || "https://tuanluupiano.com/wp-content/uploads/2026/01/avatar-facebook-mac-dinh-6.jpg",
        membership: "Thành viên Vàng",
        height: profile?.height || null,
        weight: profile?.weight || null,
        body_shape: profile?.body_shape || "",
        skin_tone: profile?.skin_tone || "",
        style_favourite: profile?.style_favourite || "",
        colors_favourite: profile?.colors_favourite || ""
      }
    });

  } catch (error) {
    console.error("===== LỖI UPDATE PROFILE =====");
    return res.status(500).json({
      success: false,
      message: error.message
    });
  }
};

module.exports = {
  getMyProfile,
  updateMyProfile
};