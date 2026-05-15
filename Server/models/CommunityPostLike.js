const mongoose = require('mongoose');
const Counter = require('./Counter');

const communityPostLikeSchema = new mongoose.Schema({
  post_like_id: {
    type: Number,
    unique: true
  },
  user_id: {
    type: Number,
    required: true,
    ref: 'User',
    index: true
  },
  post_id: {
    type: Number,
    required: true,
    ref: 'CommunityPost',
    index: true
  },
  created_at: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

communityPostLikeSchema.index({ user_id: 1, post_id: 1 }, { unique: true });

communityPostLikeSchema.pre('save', async function() {
  const doc = this;

  if (!doc.isNew) {
    return;
  }

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'post_like_id' }, 
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  doc.post_like_id = counter.seq;
});

module.exports = mongoose.model('CommunityPostLike', communityPostLikeSchema, 'community_post_likes');