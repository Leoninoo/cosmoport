package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ShipController {

    @Autowired
    ShipService shipService;

    @RequestMapping(path = "/rest/ships", method = RequestMethod.GET)
    public List<Ship> getShips(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false) ShipOrder order,
            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        return shipService.getPage(shipService.getAllSortedShips(name, planet, shipType, isUsed, after, before,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating, order), pageSize, pageNumber);
    }

    @RequestMapping(path = "/rest/ships/count", method = RequestMethod.GET)
    public Integer getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating
    ) {
        return shipService.getAllShips(name, planet, shipType, isUsed, after, before,
                minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @RequestMapping(path = "/rest/ships", method = RequestMethod.POST)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if(shipService.isShipValid(ship)) {
            ship.setRating(shipService.getRatingShip(ship));

            return new ResponseEntity<>(shipService.save(ship), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") String stringId) {
        Long shipId = convertToLong(stringId);

        if(shipId == null || shipId <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Ship ship = shipService.getShip(shipId);

        if (ship != null) {
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShip(
            @PathVariable(value = "id") String stringId,
            @RequestBody Ship ship
    ) {
        Long shipId = convertToLong(stringId);

        if(shipId == null || shipId <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if(shipService.getShip(shipId) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        final Ship updatedShip = shipService.updateShip(ship, shipService.getShip(shipId));

        if(updatedShip == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(updatedShip, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable(value = "id") String stringId) {
        Long id = convertToLong(stringId);

        if(id == null || id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        final Ship ship = shipService.getShip(id);

        if (ship != null) {
            shipService.deleteShip(ship);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Long convertToLong(String id) {
        if(id == null)
            return null;
        else try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}