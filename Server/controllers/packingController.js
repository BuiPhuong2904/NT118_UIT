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


    res.json({
      success: true,
      data: items.map(i => ({
        id: i._id.toString(),
        name: i.name,
        category: i.category,
        isPacked: i.isPacked
      }))
    });

  } catch (err) {


    res.status(500).json({
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

    res.json({
      success: true,
      data: {
        id: item._id.toString(),
        name: item.name,
        category: item.category,
        isPacked: item.isPacked
      }
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

    // KIỂM TRA ĐÃ CÓ CHƯA
    const existed = await PackingItem.find({
      trip_id: Number(tripId)
    });

    // nếu đã có checklist thì trả luôn
    if (existed.length > 0) {


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

    // tạo mới lần đầu
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
      data: created.map(i => ({
        id: i._id.toString(),
        name: i.name,
        category: i.category,
        isPacked: i.isPacked
      }))
    });

  } catch (err) {

    res.status(500).json({
      success: false,
      message: err.message
    });
  }
};