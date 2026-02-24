const mongoose = require('mongoose');
const Counter = require('./Counter');

const scheduleSchema = new mongoose.Schema({
  schedule_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true
  },
  outfit_id: {
    type: Number,
    required: true,
    ref: 'Outfit',
    index: true 
  },
  date: {
    type: Date,
    required: true
  },
  event_name: {
    type: String,
    default: null
  },
  event_type: {
    type: String,
    enum: ["Daily", "Travel", "Meeting", "Party", "Date", "Other"],
    default: "Daily"
  },
  location: {
    type: String,
    default: null
  },
  weather_note: {
    type: String,
    default: null
  }
}, {
  timestamps: true
});

scheduleSchema.index({ user_id: 1, date: 1 });

scheduleSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'schedule_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.schedule_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('Schedule', scheduleSchema, 'schedules');