package com.ntn.culinary.response;

import com.ntn.culinary.model.ContestImages;

import java.sql.Date;
import java.util.List;

public class ContestResponse {
    private int id;
    private String articleBody;
    private String headline;
    private String description;
    private Date datePublished;
    private Date dateModified;
    private String accessRole;
    private List<ContestImages> contestImages;
    private String prize;
    private boolean isFree;
    private boolean isClosed;

    public ContestResponse() {
    }

    public ContestResponse(int id, String articleBody, String headline, String description, Date datePublished, Date dateModified, List<ContestImages> contestImages, String accessRole, String prize, boolean isFree, boolean isClosed) {
        this.id = id;
        this.articleBody = articleBody;
        this.headline = headline;
        this.description = description;
        this.datePublished = datePublished;
        this.dateModified = dateModified;
        this.contestImages = contestImages;
        this.accessRole = accessRole;
        this.prize = prize;
        this.isFree = isFree;
        this.isClosed = isClosed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArticleBody() {
        return articleBody;
    }

    public void setArticleBody(String articleBody) {
        this.articleBody = articleBody;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public List<ContestImages> getContestImages() {
        return contestImages;
    }

    public void setContestImages(List<ContestImages> contestImages) {
        this.contestImages = contestImages;
    }

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
