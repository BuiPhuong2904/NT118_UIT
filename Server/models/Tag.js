const mongoose = require('mongoose');
const Counter = require('./Counter'); 

const tagSchema = new mongoose.Schema({
  tag_id: {
    type: Number,
    unique: true
  },
  tag_name: {
    type: String,
    required: true,
    trim: true 
  },
  tag_group: {
    type: String,
    required: true,
    enum: ['Season', 'Weather', 'Occasion', 'Style'], 
    index: true 
  },
  description: {
    type: String,
    default: null 
  }
}, {
  timestamps: true
});

tagSchema.pre('save', async function(next) {
  const doc = this;

  if (!doc.isNew) {
    return next();
  }

  try {
    const counter = await Counter.findByIdAndUpdate(
      { _id: 'tag_id' }, 
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    doc.tag_id = counter.seq;
    next();
  } catch (error) {
    next(error);
  }
});

module.exports = mongoose.model('Tag', tagSchema, 'tags');