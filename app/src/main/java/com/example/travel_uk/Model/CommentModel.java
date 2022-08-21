package com.example.travel_uk.Model;

public class CommentModel {

    private String comment,poster,commentid;

    public CommentModel(String comment, String poster, String commentid) {
        this.comment = comment;
        this.poster = poster;
        this.commentid = commentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public CommentModel(){

    }
}
