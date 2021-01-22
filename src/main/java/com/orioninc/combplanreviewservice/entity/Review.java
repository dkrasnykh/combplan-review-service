package com.orioninc.combplanreviewservice.entity;

import javax.persistence.Entity;

@Entity
public class Review extends EntityBase {
    private Long requestId;
    private Long userId;

    public Review() {
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
