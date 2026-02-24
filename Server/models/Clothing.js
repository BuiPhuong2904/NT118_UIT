const mongoose = require('mongoose');
const Counter = require('./Counter');

const clothingSchema = new mongoose.Schema({
  clothing_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true, 
    ref: 'User',
    index: true 
  },
  image_id: {
    type: Number, 
    required: true,
    ref: 'Image', 
    index: true
  },
  category_id: {
    type: Number,
    required: true,
    ref: 'Category', 
    index: true  
  },
  color_hex: {
    type: String,
    default: null
  },
  color_family: {
    type: String,
    default: null
  },
  material: {
    type: String,
    default: null
  },
  size: {
    type: String,
    default: null
  },
  brand_name: {
    type: String,
    default: null
  },
  is_favorite: {
    type: Boolean,
    default: false 
  },
  status: {
    type: String,
    enum: ['active', 'archived', 'in_wash'], 
    default: 'active', 
    index: true 
  },
  last_worn: {
    type: Date,
    default: null 
  }
}, {
  timestamps: true 
});

clothingSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'clothing_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.clothing_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('Clothing', clothingSchema, 'clothes');