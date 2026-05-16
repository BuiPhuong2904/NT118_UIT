const PackingItem = require('../models/PackingItem');

// ===============================
// GET checklist theo trip
// ===============================
exports.getPackingItems = async (req, res) => {
  try {
    const { tripId } = req.params;

    const items = await PackingItem.find({
      trip_id: Number(tripId)
    }).sort({ createdAt: 1 });

    res.json({ success: true, data: items });

  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};


// ===============================
// TOGGLE isPacked (FIXED)
// ===============================
exports.togglePackingItem = async (req, res) => {
  try {
    const { id } = req.params;

    const item = await PackingItem.findById(id);

    if (!item) {
      return res.status(404).json({
        success: false,
        message: "Item not found"
      });
    }

    item.isPacked = !item.isPacked;
    await item.save();

    res.json({
      success: true,
      data: item
    });

  } catch (err) {
    res.status(500).json({
      success: false,
      message: err.message
    });
  }
};

// ===============================
// CREATE checklist từ AI
// (FIX duplicate safe)
// ===============================
exports.createPackingItems = async (req, res) => {
  try {
    const { tripId, items } = req.body;

    if (!tripId || !Array.isArray(items)) {
      return res.status(400).json({
        success: false,
        message: "Invalid payload"
      });
    }

    // xoá cũ để tránh duplicate
    await PackingItem.deleteMany({ trip_id: Number(tripId) });

    const created = await PackingItem.insertMany(
      items.map(i => ({
        trip_id: Number(tripId),
        name: i.name,
        category: i.category || "AI Suggestion",
        isPacked: false
      }))
    );

    res.json({
      success: true,
      data: created
    });

  } catch (err) {
    res.status(500).json({ success: false, message: err.message });
  }
};