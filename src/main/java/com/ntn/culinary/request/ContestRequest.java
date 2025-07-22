package com.ntn.culinary.request;

import com.ntn.culinary.model.ContestImages;

import java.util.Date;
import java.util.List;

public class ContestRequest {
    private int id;
    private String headline;
    private String articleBody;
    private String description;
    private Date datePublished;
    private Date dateModified;
    private String prize;
    private boolean isFree;
    private boolean isClosed;
    private String accessRole;
    private List<ContestImagesRequest> contestImages;

    public ContestRequest() {
    }

    public ContestRequest(int id, String articleBody, String headline, String description, Date datePublished, Date dateModified, String prize, boolean isFree, boolean isClosed, String accessRole, List<ContestImagesRequest> contestImages) {
        this.id = id;
        this.articleBody = articleBody;
        this.headline = headline;
        this.description = description;
        this.datePublished = datePublished;
        this.dateModified = dateModified;
        this.prize = prize;
        this.isFree = isFree;
        this.isClosed = isClosed;
        this.accessRole = accessRole;
        this.contestImages = contestImages;
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

    public String getAccessRole() {
        return accessRole;
    }

    public void setAccessRole(String accessRole) {
        this.accessRole = accessRole;
    }

    public List<ContestImagesRequest> getContestImages() {
        return contestImages;
    }

    public void setContestImages(List<ContestImagesRequest> contestImages) {
        this.contestImages = contestImages;
    }
}
