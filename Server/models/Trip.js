const mongoose = require('mongoose');
const Counter = require('./Counter');

const tripSchema = new mongoose.Schema({
  trip_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true
  },
  destination: {
    type: String,
    required: true
  },
  start_date: {
    type: Date,
    required: true
  },
  end_date: {
    type: Date,
    required: true
  },
  trip_type: {
    type: String,
    enum: ["Du lịch", "Công tác", "Leo núi", "Khác"],
    default: "Du lịch"
  },
  transport: {
    type: String,
    enum: ["Máy bay", "Xe hơi", "Tàu hỏa"],
    default: "Máy bay"
  },
  image_url: {
    type: String,
    default: "https://i.postimg.cc/9MXZHYtp/3.jpg"
  },
  total_items: {
    type: Number,
    default: 0
  },
  packed_items: {
    type: Number,
    default: 0
  }
}, {
  timestamps: true
});

// Index
tripSchema.index({ user_id: 1, start_date: 1 });

// ✅ FIX: dùng async thì KHÔNG dùng next
tripSchema.pre('save', async function () {
  const doc = this;

  if (!doc.isNew) return;

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'trip_id' },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  doc.trip_id = counter.seq;
});
console.log("NEW TRIP MODEL LOADED");


module.exports = mongoose.model('Trip', tripSchema, 'trips');
