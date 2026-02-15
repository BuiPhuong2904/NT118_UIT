const mongoose = require('mongoose');
const Counter = require('./Counter');

const clothingTagSchema = new mongoose.Schema({
  clothing_tag_id: {
    type: Number,
    unique: true
  },
  clothing_id: {
    type: Number,
    required: true,
    ref: 'Clothing', 
    index: true  
  },
  tag_id: {
    type: Number,
    required: true,
    ref: 'Tag', 
    index: true 
  }
}, {
  timestamps: true
});

clothingTagSchema.index({ clothing_id: 1, tag_id: 1 }, { unique: true });

clothingTagSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'clothing_tag_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.clothing_tag_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('ClothingTag', clothingTagSchema);