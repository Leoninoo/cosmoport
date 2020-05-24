package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {
    List<Ship> getPage(List<Ship> ships, Integer pageSize, Integer numberPage);

    List<Ship> getAllShips(
            String name,
            String planet,
            ShipType shipType,
            Boolean isUsed,
            Long minYear,
            Long maxYear,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating
            );

    List<Ship> getAllSortedShips(
            String name,
            String planet,
            ShipType shipType,
            Boolean isUsed,
            Long minYear,
            Long maxYear,
            Double minSpeed,
            Double maxSpeed,
            Integer minCrewSize,
            Integer maxCrewSize,
            Double minRating,
            Double maxRating,
            ShipOrder shipOrder
    );

    Ship updateShip(Ship newShip, Ship oldShip);

    Double getRatingShip(Ship ship);

    Ship save(Ship ship);

    Ship getShip(Long id);

    void deleteShip(Ship ship);

    boolean isShipValid(Ship ship);
}
