const Tag = require('../models/Tag');

exports.getAllTags = async (req, res) => {
    try {
        const tags = await Tag.find().lean();
        res.status(200).json(tags);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};