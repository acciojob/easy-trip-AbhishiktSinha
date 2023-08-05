package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AirportService {

    @Autowired
    AirportRepository repoObj = new AirportRepository();

    public String addAirport(Airport airport) {
        return repoObj.addAirport(airport);
    }
    public String addPassenger(Passenger passenger) {
        return repoObj.addPassenger(passenger);
    }
    public String addFlight(Flight flight) {
        return repoObj.addFlight(flight);
    }

    public int calculateFare(int flightId) {
        return repoObj.calculateFare(flightId);
    }
    public String bookTicket(int flightId, int passengerId) {
        return repoObj.bookTicket(flightId, passengerId);
    }
    public String cancelTicket(int flightId, int passengerId) {
        return repoObj.cancelTicket(flightId, passengerId);
    }

    public int bookingsByPassenger(int passengerId) {
        return repoObj.countOfBookingsDoneByPassenger(passengerId);
    }

    public String largestAirportName() {
        return repoObj.getLargestAirport();
    }

    public double getShortestTravelTime(City fromCity, City toCity) {
        double travelTime = repoObj.getShortestTimeToTravel(fromCity, toCity);

        if(travelTime == Double.MAX_VALUE)
            return -1;
        return travelTime;
    }

    public int numberOfPeopleOnAirportOnDate(Date date, String airportName) {
        return repoObj.peopleOnAirportOnDate(date, airportName);
    }

    public int numberOfBookingsByPassenger(int passengerId) {
        return repoObj.countOfBookingsDoneByPassenger(passengerId);
    }

    public String takeOffAirport(int flightId) {
        return repoObj.getDepartureAirportName(flightId);
    }

    public int calculateRevenue(int flightId) {
        return repoObj.calculateRevenue(flightId);
    }


}
