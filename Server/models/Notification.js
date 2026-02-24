const mongoose = require('mongoose');
const Counter = require('./Counter');

const notificationSchema = new mongoose.Schema({
  noti_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true 
  },
  title: {
    type: String,
    required: true,
    trim: true
  },
  message: {
    type: String,
    required: true,
    trim: true
  },
  is_read: {
    type: Boolean,
    default: false, 
    required: true
  },
  created_at: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

notificationSchema.index({ user_id: 1, is_read: 1, created_at: -1 });

notificationSchema.index({ created_at: 1 }, { expireAfterSeconds: 2592000 });

notificationSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'noti_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.noti_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('Notification', notificationSchema, 'notifications');