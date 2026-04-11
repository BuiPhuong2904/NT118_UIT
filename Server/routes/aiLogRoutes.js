const express = require('express');
const router = express.Router();
const aiLogController = require('../controllers/aiLogController');
const authMiddleware = require('../middleware/authMiddleware');

router.use(authMiddleware);

router.post('/', aiLogController.saveLog);
router.get('/user/:userId/sessions', aiLogController.getRecentSessions);
router.get('/user/:userId/sessions/:sessionId', aiLogController.getSessionMessages);

module.exports = router;