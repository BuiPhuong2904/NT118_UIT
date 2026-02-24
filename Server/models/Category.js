const mongoose = require('mongoose');
const Counter = require('./Counter');

const categorySchema = new mongoose.Schema({
  category_id: {
    type: Number, 
    unique: true 
  },
  name: {
    type: String,
    required: true,
    trim: true 
  },
  parent_id: {
    type: Number, 
    default: null,
    index: true,  
    ref: 'Category'
  }
}, {
  timestamps: true
});

categorySchema.pre('save', async function(next) {
  const doc = this;

  // Nếu là update (sửa đổi) thì bỏ qua
  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'category_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );
    doc.category_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('Category', categorySchema, 'categories');