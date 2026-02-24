const mongoose = require('mongoose');

const counterSchema = new mongoose.Schema({
  _id: { type: String, required: true }, // Tên của bảng
  seq: { type: Number, default: 0 }      // Số hiện tại
});

module.exports = mongoose.model('Counter', counterSchema);