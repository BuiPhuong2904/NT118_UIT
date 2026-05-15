const mongoose = require('mongoose');
const Counter = require('./Counter');

const communityPostSchema = new mongoose.Schema({
  post_id: {
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
  image_url: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: null
  },
  likes_count: {
    type: Number,
    default: 0
  },
  height_ratio: {
    type: Number,
    required: true
  },
  created_at: {
    type: Date,
    default: Date.now,
    index: -1
  }
}, {
  timestamps: true
});

communityPostSchema.pre('save', async function() {
  const doc = this;

  if (!doc.isNew) {
    return;
  }

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'post_id' }, 
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  doc.post_id = counter.seq;
});

module.exports = mongoose.model('CommunityPost', communityPostSchema, 'community_posts');