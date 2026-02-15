const mongoose = require('mongoose');
const Counter = require('./Counter');

const outfitItemSchema = new mongoose.Schema({
  item_id: {
    type: Number,
    unique: true
  },
  outfit_id: {
    type: Number,
    required: true,
    ref: 'Outfit',
    index: true 
  },
  clothing_id: {
    type: Number,
    required: true,
    ref: 'Clothing',
    index: true  
  },
  position_x: {
    type: Number,
    required: true,
    default: 0
  },
  position_y: {
    type: Number,
    required: true,
    default: 0
  },
  scale: {
    type: Number,
    required: true,
    default: 1.0
  },
  z_index: {
    type: Number, 
    required: true,
    default: 0
  }
}, {
  timestamps: true
});

outfitItemSchema.index({ outfit_id: 1, clothing_id: 1 }, { unique: true });

outfitItemSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'item_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.item_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('OutfitItem', outfitItemSchema, 'outfit_items');