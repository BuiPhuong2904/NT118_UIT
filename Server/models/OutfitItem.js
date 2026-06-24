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
  item_ref_id: {
    type: Number,
    required: true,
    index: true  
  },
  item_type: {
    type: String, 
    enum: ['personal', 'system'], 
    default: 'personal' 
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
  rotation: {
    type: Number,
    required: true,
    default: 0
  },
  z_index: {
    type: Number, 
    required: true,
    default: 0
  }
}, {
  timestamps: true
});

outfitItemSchema.index({ outfit_id: 1, item_ref_id: 1, item_type: 1 });

outfitItemSchema.pre('save', async function() {
  if (!this.isNew) {
    return; 
  }

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'item_id' },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  this.item_id = counter.seq;
});

module.exports = mongoose.model('OutfitItem', outfitItemSchema, 'outfit_items');