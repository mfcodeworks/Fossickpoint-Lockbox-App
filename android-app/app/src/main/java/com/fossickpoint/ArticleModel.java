package com.fossickpoint;

/**
 * Created by dennisdarwis on 27/1/18.
 */

public class ArticleModel {
    String articleName;
    String imgURL;

    public ArticleModel(String articleName, String imgURL) {
        this.articleName = articleName;
        this.imgURL = imgURL;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
