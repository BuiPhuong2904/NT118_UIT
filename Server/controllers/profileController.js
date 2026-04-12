const User = require('../models/User');
const UserProfile = require('../models/UserProfile');

// GET /api/profile/me
const getMyProfile = async (req, res) => {
  try {
    console.log("===== GET PROFILE =====");
    console.log("req.user =", req.user);

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
        avatar: user.avatar || "",
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
    console.error(error);
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
    console.log("req.user =", req.user);
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
      avatar,
      height,
      weight,
      body_shape,
      skin_tone,
      style_favourite,
      colors_favourite
    } = req.body;

    // update bảng User
    const userUpdateData = {};
    if (username !== undefined) userUpdateData.username = username;
    if (email !== undefined) userUpdateData.email = email;
    if (avatar !== undefined) userUpdateData.avatar = avatar;

    console.log("userUpdateData =", userUpdateData);

    const updatedUser = await User.findOneAndUpdate(
      { user_id: userId },
      userUpdateData,
      { new: true }
    );

    console.log("updatedUser =", updatedUser);

    if (!updatedUser) {
      return res.status(404).json({
        success: false,
        message: 'Không tìm thấy người dùng'
      });
    }

    let profile = await UserProfile.findOne({ user_id: userId });
    console.log("profile before update =", profile);

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

    console.log("profile before save =", profile);

    await profile.save();

    console.log("profile after save =", profile);

    return res.status(200).json({
      success: true,
      message: 'Cập nhật hồ sơ thành công',
      data: {
        user_id: updatedUser.user_id,
        username: updatedUser.username,
        email: updatedUser.email,
        avatar: updatedUser.avatar || "",
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
    console.error(error);
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
