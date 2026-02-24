const mongoose = require('mongoose');
const Counter = require('./Counter'); 

const userSchema = new mongoose.Schema({
  user_id: {
    type: Number, 
    unique: true 
  },
  username: {
    type: String,
    required: true
  },
  email: {
    type: String,
    required: true,
    unique: true, 
    match: [/^.+@.+$/, 'Email không hợp lệ'] 
  },
  password_hash: {
    type: String,
    required: true
  },
  gender: {
    type: String,
    enum: ['Nam', 'Nữ', 'Khác'], 
    default: 'Khác' 
  },
  created_at: {
    type: Date,
    default: Date.now 
  }
});


userSchema.pre('save', async function(next) {
  const doc = this;
  if (!doc.isNew) {
    return next();
  }

  try {
    // Tìm bộ đếm có tên là 'user_id' và tăng seq lên 1 đơn vị
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'user_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true } 
    );

    // Gán số vừa lấy được vào user_id của người dùng mới này
    doc.user_id = counter.seq;
    next(); 
  } catch (error) {
    next(error); 
  }
});

module.exports = mongoose.model('User', userSchema, 'users');