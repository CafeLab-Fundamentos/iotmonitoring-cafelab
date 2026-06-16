package com.cafemetrix.cafelab.production.interfaces.acl;

import java.util.Optional;

public interface CoffeeproductionContextFacade {
    Optional<CoffeeLotSummary> getCoffeeLotById(Long coffeeLotId);
}
