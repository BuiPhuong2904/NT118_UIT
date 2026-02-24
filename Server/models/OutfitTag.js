const mongoose = require('mongoose');
const Counter = require('./Counter');

const outfitTagSchema = new mongoose.Schema({
  outfit_tag_id: {
    type: Number,
    unique: true
  },
  outfit_id: {
    type: Number,
    required: true,
    ref: 'Outfit',
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


outfitTagSchema.index({ outfit_id: 1, tag_id: 1 }, { unique: true });

outfitTagSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'outfit_tag_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.outfit_tag_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('OutfitTag', outfitTagSchema, 'outfit_tags');