const mongoose = require('mongoose');
const Counter = require('./Counter');

const travelChecklistSchema = new mongoose.Schema({
  travel_id: {
    type: Number,
    unique: true
  },
  schedule_id: {
    type: Number,
    required: true,
    ref: 'Schedule',
    index: true
  },
  clothing_id: {
    type: Number,
    required: true,
    ref: 'Clothing',
    index: true
  },
  is_packed: {
    type: Boolean,
    default: false,
    required: true
  }
}, {
  timestamps: true
});

travelChecklistSchema.index({ schedule_id: 1, clothing_id: 1 }, { unique: true });

travelChecklistSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'travel_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.travel_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('TravelChecklist', travelChecklistSchema, 'travel_checklists');