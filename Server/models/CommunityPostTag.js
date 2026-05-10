const mongoose = require('mongoose');
const Counter = require('./Counter');

const communityPostTagSchema = new mongoose.Schema({
  post_tag_id: {
    type: Number,
    unique: true
  },
  post_id: {
    type: Number,
    required: true,
    ref: 'CommunityPost',
    index: true
  },
  tag_id: {
    type: Number,
    required: true,
    ref: 'Tag',
    index: true
  }
}, {
  timestamps: true
});

communityPostTagSchema.pre('save', async function() {
  const doc = this;

  if (!doc.isNew) {
    return;
  }

  const counter = await Counter.findByIdAndUpdate(
    { _id: 'post_tag_id' }, 
    { $inc: { seq: 1 } },
    { new: true, upsert: true }
  );

  doc.post_tag_id = counter.seq;
});

module.exports = mongoose.model('CommunityPostTag', communityPostTagSchema, 'community_post_tags');