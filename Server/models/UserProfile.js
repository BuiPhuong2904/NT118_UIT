const mongoose = require('mongoose');
const Counter = require('./Counter'); // <--- 1. Import bộ đếm

const userProfileSchema = new mongoose.Schema({
  profiles_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    unique: true, 
    ref: 'User' 
  },
  height: {
    type: Number,
    min: 0    
  },
  weight: {
    type: Number,
    min: 0
  },
  body_shape: {
    type: String,
    trim: true  
  },
  skin_tone: {
    type: String,
    trim: true
  },
  style_favourite: {
    type: String,
    trim: true
  },
  colors_favourite: {
    type: String,
    trim: true
  }
}, {
  timestamps: true 
});

// tự động tăng profiles_id 
userProfileSchema.pre('save', async function () {
  const doc = this;

  if (!doc.isNew) {
    return;
  }

  const counter = await Counter.findByIdAndUpdate(
    'profiles_id',
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  doc.profiles_id = counter.seq;
});



module.exports = mongoose.model('UserProfile', userProfileSchema, 'user_profiles');