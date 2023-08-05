package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AirportRepository {

    Map<String, Airport> airportMap = new HashMap<>();
    Map<Integer, Passenger> passengerMap = new HashMap<>();
    Map<Integer, Flight> flightMap = new HashMap<>();

    Map<String, List<Flight>> cityDeparturesMap = new HashMap<>();
    Map<String, List<Flight>> cityArrivalsMap = new HashMap<>();

    Map<Integer, List<Passenger>> bookingsByFlight = new HashMap<>();
    Map<Integer, List<Flight>> bookingsByPassenger = new HashMap<>();

    public String addAirport(Airport airport) {
        String name = airport.getAirportName();
        airportMap.put(name, airport);

        return "SUCCESS";
    }
    public String addPassenger(Passenger newPassenger) {
        int id = newPassenger.getPassengerId();
        if(passengerMap.containsKey(id))
            return "FAILURE";

        passengerMap.put(id, newPassenger);
        return "SUCCESS";
    }
    public String addFlight(Flight newFlight) {
        int id = newFlight.getFlightId();
        flightMap.put(id, newFlight);

        String departureCityName = newFlight.getFromCity().toString();
        String arrivalCityName = newFlight.getToCity().toString();

        List<Flight> departingFLightsList= cityDeparturesMap.getOrDefault(departureCityName, new ArrayList<>());
        departingFLightsList.add(newFlight);
        cityDeparturesMap.put(departureCityName, departingFLightsList);

        List<Flight> arrivingFlightsList = cityArrivalsMap.getOrDefault(arrivalCityName, new ArrayList<>());
        arrivingFlightsList.add(newFlight);
        cityArrivalsMap.put(arrivalCityName, arrivingFlightsList);

        return "SUCCESS";
    }

    public int calculateFare(int flightId) {

        int existingBookings = bookingsByFlight.getOrDefault(flightId, new ArrayList<>()).size();

        int fare = 3000 + existingBookings * 50;

        return fare;
    }

    public String bookTicket(int flightId, int passengerId) {
        if(!flightMap.containsKey(flightId) || !passengerMap.containsKey(passengerId)) return "FAILURE";

        Flight flight = flightMap.get(flightId);
        Passenger passenger = passengerMap.get(passengerId);

        List<Passenger> bookingsList_Flight = bookingsByFlight.getOrDefault(flightId, new ArrayList<>());
        int numberOfBookings_Flight = bookingsList_Flight.size();

        List<Flight> bookingsList_Passenger = bookingsByPassenger.getOrDefault(passengerId, new ArrayList<>());

        if(numberOfBookings_Flight >= flight.getMaxCapacity())
            return "FAILURE";

        if(bookingsList_Passenger.contains(flight))
            return "FAILURE";

        bookingsList_Flight.add(passenger);
        bookingsByFlight.put(flightId, bookingsList_Flight);

        bookingsList_Passenger.add(flight);
        bookingsByPassenger.put(passengerId, bookingsList_Passenger);

        return "SUCCESS";
    }

    public String cancelTicket(int flightId, int passengerId) {
        if(!flightMap.containsKey(flightId) || !passengerMap.containsKey(passengerId)) return "FAILURE";

        Flight flight = flightMap.get(flightId);
        Passenger passenger = passengerMap.get(passengerId);

        List<Passenger> bookingsList_Flight = bookingsByFlight.getOrDefault(flightId, new ArrayList<>());
        List<Flight> bookingsList_Passenger = bookingsByPassenger.getOrDefault(passengerId, new ArrayList<>());

        if(!bookingsList_Passenger.contains(flight)) return "FAILURE";

        bookingsList_Flight.remove(passenger);
        bookingsList_Passenger.remove(flight);
        return "SUCCESS";
    }

    public int countOfBookingsDoneByPassenger(int passengerId) {
        return bookingsByPassenger.getOrDefault(passengerId, new ArrayList<>()).size();
    }


    public String getLargestAirport() {

        String largestAirport = "";
        int mostTerminals = -1;
        for(Airport airport : airportMap.values()) {
            int terminals = airport.getNoOfTerminals();

            if(terminals > mostTerminals) {
                mostTerminals = terminals;
                largestAirport = airport.getAirportName();
            }
            else if(terminals == mostTerminals) {
                if(airport.getAirportName().compareTo(largestAirport) < 0) {
                    largestAirport = airport.getAirportName();
                }
            }
        }

        return largestAirport;
    }
    /*
    class Pair {
        String cityName;
        String path;
        double durationSoFar;

        public Pair(String cityName, String path, double durationSoFar) {
            this.cityName = cityName;
            this.path = path;
            this.durationSoFar = durationSoFar;
        }
    }*/
    public double getShortestTimeToTravel(City fromCity, City toCity) {
        String src = fromCity.toString();
        String dest = toCity.toString();

        /*HashSet<String> vis = new HashSet<>();

        PriorityQueue<Pair> pq = new PriorityQueue<>((a,b)->{
            return (int)(a.durationSoFar-b.durationSoFar);
        });

        pq.add(new Pair(src, src, 0.0));

        while(pq.size() > 0) {

            Pair rpair = pq.remove();
            String cityName = rpair.cityName;
            String pathSoFar = rpair.path;
            double duration = rpair.durationSoFar;

            if(vis.contains(cityName)) continue;

            if(cityName.equals(dest))
                return duration;

            vis.add(cityName);

            for(Flight flight : cityDepartures.get(cityName)) {
                String nbr = flight.getToCity().toString();
                double nbrFlightDuration = flight.getDuration();

                if(!vis.contains(nbr)) {
                    pq.add(new Pair(nbr, pathSoFar +" -> "+nbr, duration + nbrFlightDuration));
                }
            }
        }
        return -1.0;
        */

        double leastTime = Double.MAX_VALUE;
        for(Flight flight : cityDeparturesMap.get(src)) {

            String flightDest = flight.getToCity().toString();
            if(flightDest.equals(dest)) {
                leastTime = Math.min(leastTime, flight.getDuration());
            }
        }

        return leastTime;
    }

    public int peopleOnAirportOnDate(Date date, String airportName) {
        Airport airport = airportMap.get(airportName);

        String cityName = airport.getCity().toString();

        int arrivingPassengers = 0;
        int departingPassengers = 0;

        //count passengers on arriving flights
        for(Flight flight : cityArrivalsMap.get(cityName)) {
            if(flight.getFlightDate().equals(date)) {

                int id = flight.getFlightId();
                int passengersOnBoard = bookingsByFlight.getOrDefault(id, new ArrayList<>()).size();
                arrivingPassengers += passengersOnBoard;
            }
        }

        //count passengers waiting for departing flights
        for(Flight flight : cityDeparturesMap.get(cityName)) {

            if(flight.getFlightDate().equals(date)) {

                int id = flight.getFlightId();
                int passengersOnBoard = bookingsByFlight.getOrDefault(id, new ArrayList<>()).size();
                departingPassengers += passengersOnBoard;
            }
        }
        int totalPassengers = arrivingPassengers + departingPassengers;
        return totalPassengers;
    }

    public String getDepartureAirportName(int flightId) {
        Flight flight = flightMap.get(flightId);
        String cityName = flight.getFromCity().toString();

        for(Airport airport : airportMap.values()) {
            if(airport.getCity().toString().equals(cityName)){
                return airport.getAirportName();
            }
        }
        return "";
    }

    public int calculateRevenue(int flightId) {
        int n = bookingsByFlight.getOrDefault(flightId, new ArrayList<>()).size();

        if(n == 0) return 0;

        int revenue = (3000 * n) + 50 * n * (n * (n + 1)/2);

        return revenue;
    }
}
