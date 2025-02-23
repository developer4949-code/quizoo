package com.example.zappycode.quizapp.Models;
public class SubCategoryModel {
    private String CategoryName;
    private String key;

    public SubCategoryModel(String CategoryName,String key) {
        this.CategoryName=CategoryName;
        this.key = key;
    }

    public SubCategoryModel(String categoryName) {
        CategoryName = categoryName;
    }

    // Getters and setters
    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String subjectName) {
        this.CategoryName = subjectName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SubCategoryModel() {
    }
}
