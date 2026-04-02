const mongoose = require('mongoose');
const Counter = require('./Counter');

const wishlistSchema = new mongoose.Schema({
  wishlist_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true
  },
  template_id: {
    type: Number,
    ref: 'SystemClothing',
    default: null,
    index: true
  },
  item_name: {
    type: String,
    required: true,
    trim: true
  },
  image_url: {
    type: String,
    default: null
  },
  price_estimate: {
    type: Number, 
    default: null,
    min: 0
  },
  link_store: {
    type: String,
    default: null,
    trim: true
  },
  status: {
    type: String,
    enum: ['pending', 'purchased'], 
    default: 'pending', 
    index: true 
  }
}, {
  timestamps: true
});

wishlistSchema.pre('save', async function() {
  const doc = this;

  if (!doc.isNew) {
    return;
  }

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'wishlist_id' },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  doc.wishlist_id = counter.seq;
});

module.exports = mongoose.model('Wishlist', wishlistSchema, 'wishlists');