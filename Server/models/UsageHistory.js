const mongoose = require('mongoose');
const Counter = require('./Counter');

const usageHistorySchema = new mongoose.Schema({
  usage_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
  },
  clothing_id: {
    type: Number,
    required: true,
    ref: 'Clothing',
    index: true
  },
  used_at: {
    type: Date,
    required: true,
    default: Date.now 
  }
}, {
  timestamps: true 
});

usageHistorySchema.index({ user_id: 1, used_at: -1 });

usageHistorySchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'usage_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.usage_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('UsageHistory', usageHistorySchema);