const mongoose = require('mongoose');
const Counter = require('./Counter'); 

const outfitSchema = new mongoose.Schema({
  outfit_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true 
  },
  name: {
    type: String,
    required: true,
    trim: true
  },
  description: {
    type: String,
    default: null
  },
  rating: {
    type: Number,
    min: 1,
    max: 5,
    default: null
  },
  is_ai_suggested: {
    type: Boolean,
    default: false 
  },
  image_preview_url: {
    type: String,
    default: null
  },
  created_at: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

outfitSchema.index({ user_id: 1, rating: -1 });

outfitSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'outfit_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.outfit_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('Outfit', outfitSchema);