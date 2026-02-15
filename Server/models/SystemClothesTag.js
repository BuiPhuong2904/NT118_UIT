const mongoose = require('mongoose');
const Counter = require('./Counter');

const systemClothesTagSchema = new mongoose.Schema({
  system_tag_id: {
    type: Number,
    unique: true
  },
  template_id: {
    type: Number,
    required: true,
    ref: 'SystemClothing', 
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

systemClothesTagSchema.index({ template_id: 1, tag_id: 1 }, { unique: true });

systemClothesTagSchema.pre('save', async function(next) {
  const doc = this;

  // Nếu là update (sửa đổi) thì bỏ qua
  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'system_tag_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.system_tag_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('SystemClothesTag', systemClothesTagSchema, 'system_clothes_tags');