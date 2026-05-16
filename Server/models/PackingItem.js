const mongoose = require('mongoose');

const packingItemSchema = new mongoose.Schema({
  trip_id: {
    type: Number,
    required: true,
    index: true
  },
  name: String,
  category: String,
  isPacked: {
    type: Boolean,
    default: false
  }
}, { timestamps: true });

module.exports = mongoose.model('PackingItem', packingItemSchema);