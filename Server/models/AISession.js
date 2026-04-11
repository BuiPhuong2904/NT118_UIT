const mongoose = require('mongoose');

const aiSessionSchema = new mongoose.Schema({
  session_id: {
    type: String,
    required: true,
    unique: true,
    index: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true
  },
  title: {
    type: String,
    default: null
  }
}, {
  timestamps: { createdAt: 'created_at', updatedAt: 'updated_at' } 
});

// tối ưu tốc độ load Sidebar trên App
aiSessionSchema.index({ updated_at: -1 });

module.exports = mongoose.model('AISession', aiSessionSchema, 'ai_sessions');