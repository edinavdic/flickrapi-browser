package com.github.flickrapidatabase;

import java.io.Serializable;

class Photo implements Serializable{

    private static final long serialVersionUID = 1L;

    private String mTitle, mAuthor, mAuthorId, mLink, mTags, mImage;

    public Photo(String Title, String Author, String AuthorId, String Link, String Tags, String Image) {
        this.mTitle = Title;
        this.mAuthor = Author;
        this.mAuthorId = AuthorId;
        this.mLink = Link;
        this.mTags = Tags;
        this.mImage = Image;
    }

    String getTitle() {
        return mTitle;
    }

    String getAuthor() {
        return mAuthor;
    }

    String getAuthorId() {
        return mAuthorId;
    }

    String getLink() {
        return mLink;
    }

    String getTags() {
        return mTags;
    }

    String getImage() {
        return mImage;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "Title='" + mTitle + '\'' +
                ", Author='" + mAuthor + '\'' +
                ", AuthorId='" + mAuthorId + '\'' +
                ", Link='" + mLink + '\'' +
                ", Tags='" + mTags + '\'' +
                ", Image='" + mImage + '\'' +
                '}';
    }
}
