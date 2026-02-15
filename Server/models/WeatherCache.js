const mongoose = require('mongoose');
const Counter = require('./Counter');

const weatherCacheSchema = new mongoose.Schema({
  weather_id: {
    type: Number,
    unique: true
  },
  location_name: {
    type: String,
    required: true,
    trim: true,
    index: true
  },
  latitude: {
    type: Number,
    required: true
  },
  longitude: {
    type: Number,
    required: true
  },
  temp: {
    type: Number,
    required: true
  },
  condition: {
    type: String,
    required: true
  },
  icon_url: {
    type: String,
    required: true
  },
  expired_at: {
    type: Date,
    required: true
  }
}, {
  timestamps: true
});

weatherCacheSchema.index({ expired_at: 1 }, { expireAfterSeconds: 0 });

weatherCacheSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'weather_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.weather_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('WeatherCache', weatherCacheSchema);