package it.polimi.tiw.beans;

public class CategoryChanges {
    private String categoryId;
    private String newId;
    private String newFatherId;

    public String getNewFatherId() {
        return newFatherId;
    }

    public void setNewFatherId(String newFatherId) {
        this.newFatherId = newFatherId;
    }

    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}