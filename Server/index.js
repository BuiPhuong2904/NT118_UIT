const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json());

const MONGO_URI = process.env.MONGO_URI;

mongoose.connect(MONGO_URI)
    .then(() => console.log('Đã kết nối MongoDB thành công!'))
    .catch((err) => console.error('Lỗi kết nối:', err));

app.get('/', (req, res) => {
    res.send('Server Smart Fashion đang chạy!');
});

app.listen(PORT, () => {
    console.log(`Server đang chạy tại http://localhost:${PORT}`);
});