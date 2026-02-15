const mongoose = require('mongoose');
const Counter = require('./Counter');

const systemClothingSchema = new mongoose.Schema({
  template_id: {
    type: Number,
    unique: true
  },
  category_id: {
    type: Number,
    required: true,
    ref: 'Category', 
    index: true   
  },
  image_url: {
    type: String,
    required: true
  },
  color_hex: {
    type: String,
    default: null
  },
  color_family: {
    type: String,
    default: null
  },
  description: {
    type: String,
    default: null
  }
}, {
  timestamps: true
});

systemClothingSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'template_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.template_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('SystemClothing', systemClothingSchema, 'system_clothes');