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
  reset_token: {
    type: String
  },

  reset_token_expire: {
    type: Date
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

userSchema.pre('save', async function () {
  if (!this.isNew) return;

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'user_id' },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  this.user_id = counter.seq;
});

module.exports = mongoose.model('User', userSchema, 'users');
