package com.cafemetrix.cafelab.production.interfaces.acl;

public class CoffeeLotSummary {

    private final Long id;
    private final Long userId;

    public CoffeeLotSummary(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }
}
