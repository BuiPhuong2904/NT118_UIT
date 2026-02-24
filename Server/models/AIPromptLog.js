const mongoose = require('mongoose');
const Counter = require('./Counter');

const aiPromptLogSchema = new mongoose.Schema({
  ailog_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true 
  },
  input_prompt: {
    type: String,
    required: true 
  },
  gemini_raw_response: {
    type: String,
    default: null
  },
  weather_context: {
    type: String,
    default: null 
  },
  created_at: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true 
});

aiPromptLogSchema.index({ created_at: -1 });

aiPromptLogSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'ailog_id' },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.ailog_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('AIPromptLog', aiPromptLogSchema, 'ai_prompt_logs');