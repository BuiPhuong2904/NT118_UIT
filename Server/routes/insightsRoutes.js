const express = require('express');
const router = express.Router();
const insightsController = require('../controllers/insightsController');

router.get('/user/:userId', insightsController.getUserInsights);

module.exports = router;