const Category = require('../models/Category');

// Lấy danh sách TẤT CẢ danh mục
exports.getAllCategories = async (req, res) => {
    try {
        // CẬP NHẬT: Xóa { parent_id: null } để lấy cả thư mục cha lẫn con
        const categories = await Category.find({}).sort({ category_id: 1 });        
        res.status(200).json(categories);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// Lấy chi tiết 1 danh mục theo ID (GET)
exports.getCategoryById = async (req, res) => {
    try {
        const category = await Category.findOne({ category_id: req.params.id });
        if (!category) {
            return res.status(404).json({ message: "Không tìm thấy danh mục này" });
        }
        res.status(200).json(category);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// Thêm danh mục mới (POST)
exports.createCategory = async (req, res) => {
    try {
        const newCategory = new Category(req.body);
        const savedCategory = await newCategory.save();
        res.status(201).json(savedCategory);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// Cập nhật thông tin danh mục (PUT)
exports.updateCategory = async (req, res) => {
    try {
        const updatedCategory = await Category.findOneAndUpdate(
            { category_id: req.params.id }, 
            req.body, 
            { new: true } 
        );
        
        if (!updatedCategory) {
            return res.status(404).json({ message: "Không tìm thấy danh mục cần sửa" });
        }
        res.status(200).json(updatedCategory);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// Xóa danh mục (DELETE)
exports.deleteCategory = async (req, res) => {
    try {
        const deletedCategory = await Category.findOneAndDelete({ category_id: req.params.id });
        
        if (!deletedCategory) {
            return res.status(404).json({ message: "Không tìm thấy danh mục cần xóa" });
        }
        res.status(200).json({ message: "Đã xóa danh mục thành công" });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};