const PackingItem = require('../models/PackingItem');
const Trip = require('../models/Trip');

// ===============================
// GET checklist theo trip
// ===============================
exports.getPackingItems = async (req, res) => {
  try {
    const { tripId } = req.params;

    const items = await PackingItem.find({
      trip_id: Number(tripId)
    }).sort({ createdAt: 1 });

    return res.json({
      success: true,
      data: items.map(i => ({
        id: i._id.toString(),
        name: i.name,
        category: i.category,
        isPacked: i.isPacked
      }))
    });

  } catch (err) {
    return res.status(500).json({
      success: false,
      message: err.message
    });
  }
};


// ===============================
// CREATE checklist từ AI (ONLY ONCE)
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

    const existed = await PackingItem.find({
      trip_id: Number(tripId)
    });

    // ===============================
    // CASE 1: ĐÃ CÓ DATA → KHÔNG CREATE LẠI
    // ===============================
    if (existed.length > 0) {

      const total = existed.length;
      const packed = existed.filter(i => i.isPacked).length;

      await Trip.findOneAndUpdate(
        { trip_id: Number(tripId) },
        {
          $set: {
            total_items: total,
            packed_items: packed
          }
        }
      );

      return res.json({
        success: true,
        data: existed.map(i => ({
          id: i._id.toString(),
          name: i.name,
          category: i.category,
          isPacked: i.isPacked
        }))
      });
    }

    // ===============================
    // CASE 2: CREATE NEW AI CHECKLIST
    // ===============================
    const created = await PackingItem.insertMany(
      items.map(i => ({
        trip_id: Number(tripId),
        name: i.name,
        category: i.category || "other",
        isPacked: false
      }))
    );


    const total = created.length;

    await Trip.findOneAndUpdate(
      { trip_id: Number(tripId) },
      {
        $set: {
          total_items: total,
          packed_items: 0
        }
      }
    );

    return res.json({
      success: true,
      data: created.map(i => ({
        id: i._id.toString(),
        name: i.name,
        category: i.category,
        isPacked: i.isPacked
      }))
    });

  } catch (err) {
    return res.status(500).json({
      success: false,
      message: err.message
    });
  }
};


// ===============================
// TOGGLE isPacked
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

    const tripId = item.trip_id;

    const total = await PackingItem.countDocuments({ trip_id: tripId });

    const packed = await PackingItem.countDocuments({
      trip_id: tripId,
      isPacked: true
    });

    const updatedTrip = await Trip.findOneAndUpdate(
      { trip_id: tripId },
      {
        $set: {
          packed_items: packed,
          total_items: total
        }
      },
      { new: true }
    );

    return res.json({
      success: true,
      data: {
        id: item._id.toString(),
        name: item.name,
        category: item.category,
        isPacked: item.isPacked
      }
    });

  } catch (err) {
    return res.status(500).json({
      success: false,
      message: err.message
    });
  }
};