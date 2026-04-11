const mongoose = require('mongoose');
const Counter = require('./Counter');

const aiPromptLogSchema = new mongoose.Schema({
  ailog_id: {
    type: Number,
    unique: true
  },
  session_id: {
    type: String,
    required: true,
    ref: 'AISession',
    index: true
  },
  input_prompt: {
    type: String,
    required: true 
  },
  input_image_url: {
    type: String,
    default: null
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
});

aiPromptLogSchema.index({ created_at: -1 });
aiPromptLogSchema.pre('save', async function() {
  const doc = this;

  if (!doc.isNew) {
    return;
  }

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'ailog_id' },
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  doc.ailog_id = counter.seq;
});

module.exports = mongoose.model('AIPromptLog', aiPromptLogSchema, 'ai_prompt_logs');