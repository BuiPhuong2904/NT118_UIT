const mongoose = require('mongoose');
const Counter = require('./Counter'); // <--- 1. Import bộ đếm

const imageSchema = new mongoose.Schema({
  image_id: {
    type: Number,
    unique: true 
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true 
  },
  url_original: {
    type: String,
    required: true
  },
  url_no_bg: {
    type: String,
    default: null 
  },
  storage_type: {
    type: String, 
    default: null 
  },
  created_at: {
    type: Date,
    default: Date.now
  }
});

imageSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'image_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.image_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('Image', imageSchema);