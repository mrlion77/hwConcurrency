package com.gmail.tarkhanov.lev;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Lev Tarkhanov on 29.09.2017.
 */
public class App {

    private Map<String, Double> cityData = new HashMap<>();

    public static void main(String[] args) {

        App app = new App();
        JSONReader jsonReader = new JSONReader();
        List<City> jsonCityList = jsonReader.getJsonCityList();
        Long start;
        Long end;

        app.showTaskHeader("NO THREADS");
        start = System.nanoTime();
        app.getTempsNoThread(jsonCityList);
        end = System.nanoTime();
        app.showInfo(app, jsonCityList, start, end);
        app.clearCityData();

        app.showTaskHeader("BY THREADS");
        start = System.nanoTime();
        app.getTempsByThreads(jsonCityList);
        end = System.nanoTime();
        app.showInfo(app, jsonCityList, start, end);
        app.clearCityData();

        app.showTaskHeader("BY FUTURES");
        start = System.nanoTime();
        app.getTempsByFutures(jsonCityList);
        end = System.nanoTime();
        app.showInfo(app, jsonCityList, start, end);
        app.clearCityData();

    }


    private void getTempsNoThread(List<City> jsonCityList) {
        for (City city : jsonCityList) {
            WeatherGetter wg = new WeatherGetter();
            Float temperature = 0.0F;
            try {
                temperature = wg.getTemperatureByCityCode(city.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.cityData.put(city.getName(), roundToDouble(Double.valueOf(temperature), 2));
        }
    }


    private void getTempsByThreads(List<City> jsonCityList) {
        List<Thread> threads = new ArrayList<>();
        for (City city : jsonCityList) {
            WeatherGetterThread wgt = new WeatherGetterThread(city);
            threads.add(wgt);
            wgt.start();
        }
        for (Thread nextThread : threads) {
            try {
                nextThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Thread nextThread : threads) {
            this.cityData.put(((WeatherGetterThread) nextThread).getCity().getName(), roundToDouble(Double.valueOf(((WeatherGetterThread) nextThread).getTemperature()), 2 ));
        }

    }

    private void getTempsByFutures(List<City> jsonCityList) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (City city : jsonCityList) {
            Callable<Float> task = () -> {
                WeatherGetter wg = new WeatherGetter();
                Float temperature = 0.0F;
                try {
                    temperature = wg.getTemperatureByCityCode(city.getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return temperature;
            };
            Future<Float> future = executor.submit(task);
            try {
                this.cityData.put(city.getName(), roundToDouble(Double.valueOf(future.get()), 2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
        }
    }


    private void clearCityData() {
        this.cityData.clear();
    }


    private static double roundToDouble(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }


    private static double getTimeSpent(Long start, Long end) {
        return roundToDouble(( end - start ) / Math.pow( 10, 9 ) , 2 );
    }


    private static double getAverageTemperature(App app, List<City> jsonCityList) {
        Double averageTemp = 0.0;
        for (Map.Entry<String, Double> city : app.cityData.entrySet()) {
            averageTemp += city.getValue();
        }
        averageTemp = averageTemp / jsonCityList.size();
        return roundToDouble(averageTemp, 2);
    }


    private void showInfo(App app, List<City> jsonCityList, Long start, Long end) {
        System.out.println("City data: " + app.cityData);
        System.out.println("Average temperature: " + getAverageTemperature(app, jsonCityList));
        System.out.println("Cities proceeded: " + jsonCityList.size());
        System.out.println("Time spent: " + getTimeSpent(start, end));
        System.out.println("\n");
    }


    private void showTaskHeader(String header) {
        System.out.println("----------------");
        System.out.println(header);
        System.out.println("----------------");
    }

}
