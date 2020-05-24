package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class ShipServiceImpl implements ShipService {

    @Autowired
    ShipRepository shipRepository;

    @Override
    public List<Ship> getPage(List<Ship> ships, Integer pageSize, Integer numberPage) {
        Integer page = numberPage == null ? 0 : numberPage;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > ships.size()) to = ships.size();
        return ships.subList(from, to);
    }

    @Override
    public List<Ship> getAllShips(String name, String planet,
                                  ShipType shipType, Boolean isUsed,
                                  Long minYear, Long maxYear,
                                  Double minSpeed, Double maxSpeed,
                                  Integer minCrewSize, Integer maxCrewSize,
                                  Double minRating, Double maxRating) {
        List<Ship> allShips = new ArrayList<>();

        final Date minDate = (minYear == null) ? null : new Date(minYear);
        final Date maxDate = (maxYear == null) ? null : new Date(maxYear);
        shipRepository.findAll().forEach(ship -> {
            if(name != null && !ship.getName().contains(name)) return;
            if(planet != null && !ship.getPlanet().contains(planet)) return;
            if(shipType != null && !ship.getShipType().equals(shipType)) return;
            if(isUsed != null && !ship.getUsed().equals(isUsed)) return;
            if(minDate != null && ship.getProdDate().before(minDate)) return;
            if(maxDate != null && ship.getProdDate().after(maxDate)) return;
            if(minSpeed != null && ship.getSpeed() < minSpeed) return;
            if(maxSpeed != null && ship.getSpeed() > maxSpeed) return;
            if(minCrewSize != null && ship.getCrewSize() < minCrewSize) return;
            if(maxCrewSize != null && ship.getCrewSize() > maxCrewSize) return;
            if(minRating != null && ship.getRating() < minRating) return;
            if(maxRating != null && ship.getRating() > maxRating) return;

            allShips.add(ship);
        });

        return allShips;
    }

    @Override
    public List<Ship> getAllSortedShips(String name, String planet,
                                        ShipType shipType, Boolean isUsed,
                                        Long minYear, Long maxYear,
                                        Double minSpeed, Double maxSpeed,
                                        Integer minCrewSize, Integer maxCrewSize,
                                        Double minRating, Double maxRating, ShipOrder shipOrder) {

        List<Ship> sortedShips = getAllShips(name, planet, shipType, isUsed, minYear, maxYear, minSpeed, maxSpeed,
                minCrewSize, maxCrewSize, minRating, maxRating);

        if (shipOrder != null) {
            switch (shipOrder) {
                case ID:
                    sortedShips.sort(Comparator.comparing(Ship::getId));
                    break;
                case SPEED:
                    sortedShips.sort(Comparator.comparing(Ship::getSpeed));
                    break;
                case DATE:
                    sortedShips.sort(Comparator.comparing(Ship::getProdDate));
                    break;
                case RATING:
                    sortedShips.sort(Comparator.comparing(Ship::getRating));
                    break;
            }
        }

        return sortedShips;
    }

    @Override
    public Ship getShip(Long id) {
        if (shipRepository.findById(id).isPresent())
            return shipRepository.findById(id).get();
        return null;
    }

    @Override
    public Ship updateShip(Ship newShip, Ship oldShip) {
        if(newShip.getName() != null)
            oldShip.setName(newShip.getName());
        if(newShip.getPlanet() != null)
            oldShip.setPlanet(newShip.getPlanet());
        if(newShip.getShipType() != null)
            oldShip.setShipType(newShip.getShipType());
        if(newShip.getUsed() != null)
            oldShip.setUsed(newShip.getUsed());
        if(newShip.getProdDate() != null)
            oldShip.setProdDate(newShip.getProdDate());
        if(newShip.getSpeed() != null)
            oldShip.setSpeed(newShip.getSpeed());
        if(newShip.getCrewSize() != null)
            oldShip.setCrewSize(newShip.getCrewSize());
        if(newShip.getRating() != null)
            oldShip.setRating(newShip.getRating());

        if(!isShipValid(oldShip))
            return null;

        System.out.println(oldShip.getName());

        oldShip.setRating(getRatingShip(oldShip));

        return shipRepository.save(oldShip);
    }

    @Override
    public void deleteShip(Ship ship) {
        shipRepository.delete(ship);
    }

    @Override
    public Ship save(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public Double getRatingShip(Ship ship) {
        if(ship.getUsed() == null)
            ship.setUsed(false);

        final double k = ship.getUsed() ? 0.5 : 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        double rating = (80 * ship.getSpeed() * k) / (3019 - calendar.get(Calendar.YEAR) + 1);
        double scale = Math.pow(10, 2);
        return Math.round(rating * scale) / scale;
    }

    @Override
    public boolean isShipValid(Ship ship) {

        if(ship.getName() == null || ship.getName().isEmpty() || ship.getName().length() > 50)
            return false;

        if(ship.getPlanet() == null || ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50)
            return false;

        if(ship.getProdDate() == null || ship.getProdDate().before(getMinDate()) || ship.getProdDate().after(getMaxDate()))
            return false;

        if(ship.getCrewSize() == null || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
            return false;

        if(ship.getSpeed() == null || ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)
            return false;

        return true;
    }

    private Date getMinDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2800);
        return calendar.getTime();
    }

    private Date getMaxDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 3019);
        return calendar.getTime();
    }
}
