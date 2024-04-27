import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.*;

public class M11HomeworkDriverWithStreams {
    private static Map<Integer, Integer> uSPopulationByYearMap;

    public static void main(String[] args) {

        uSPopulationByYearMap = new HashMap<>();
        fillMapWithUSPopulationData(uSPopulationByYearMap);

        List<EmploymentData> employmentDataList = new ArrayList<>();
        Map<Integer, List<Integer>> numberOfEmployeesByYearMap = new HashMap<>();
        Map<EmploymentData.Decade, List<EmploymentData>> employmentDataByDecadeMap = new HashMap<>();
        Map<Integer, Integer> averageNumEMployeesPerYearMap = new HashMap<>();
        Map<Integer, List<Double>> yearAveragePopulationMap = new HashMap<>();

        fillListAndMapWithEmploymentData(employmentDataList, numberOfEmployeesByYearMap,
                employmentDataByDecadeMap, averageNumEMployeesPerYearMap, yearAveragePopulationMap);


        // QUESTION 1: What is three lowest employment rates in the 1980 - 2024 period
        // considering the U.S. population data for those years.
        System.out.println("Three Lowest Employment Rates in the 1980 - 2024 Period:");
        yearAveragePopulationMap.entrySet().stream()
                .sorted(Comparator.comparingDouble(e -> e.getValue().get(2)))
                .limit(3)
                .forEach(e -> System.out.printf("Year: %d, Employment Rate: %.2f%%\n", e.getKey(), e.getValue().get(2)));
        System.out.println();


        // QUESTION 2: What is the average employment rate for each decade from the 1980s to the 2020s?
        Map<EmploymentData.Decade, Double> averageEmploymentRateByDecade = getAverageValuesByDecade(employmentDataList);
        System.out.println("Average Employment Rate for Each Decade From the 1980s to the 2020s:");
        averageEmploymentRateByDecade.forEach((decade, rate) ->
                System.out.printf("Decade: %s, Employment Rate: %.2f%%\n", decade.getDisplayDecade(), rate));

    }


    private static void fillMapWithUSPopulationData(Map<Integer, Integer> uSPopulationByYearMap) {
        try (Stream<String> lines = Files.lines(Paths.get("united-states-population-2024-03-10.csv"))) {
            lines.skip(1)
                    .map(line -> line.split(","))
                    .forEach(data -> {
                        int year = Integer.parseInt(data[0].substring(0, 4));
                        int population = Integer.parseInt(data[1]);
                        uSPopulationByYearMap.put(year, population);
                    });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    private static void fillListAndMapWithEmploymentData(List<EmploymentData> employmentDataList,
                                                         Map<Integer, List<Integer>> numberOfEmployeesByYearMap,
                                                         Map<EmploymentData.Decade, List<EmploymentData>> employmentRateByDecadeMap,
                                                         Map<Integer, Integer> averageNumEmployeesPerYearMap,
                                                         Map<Integer, List<Double>> yearAveragePopulationMap) {

        try (Stream<String> lines = Files.lines(Paths.get("total-employees-1980-2024.csv"))) {

            lines.skip(1)
                    .map(line -> line.split(","))
                    .forEach(data -> {
                        int year = Integer.parseInt(data[1]);
                        String month = data[2];
                        int value = Integer.parseInt(data[4]);
                        int thirdDigit = (year % 100) / 10;
                        EmploymentData.Decade decade = getDecade(thirdDigit);
                        Integer population = uSPopulationByYearMap.get(year);
                        EmploymentData employmentData = new EmploymentData(year, month, value, decade, population);
                        employmentDataList.add(employmentData);

                        numberOfEmployeesByYearMap.computeIfAbsent(year, k -> new ArrayList<>()).add(value);

                        employmentRateByDecadeMap.computeIfAbsent(decade, k -> new ArrayList<>()).add(employmentData);

                        int average = getAverageNumEmployeesPerYear(numberOfEmployeesByYearMap.get(year));
                        averageNumEmployeesPerYearMap.put(year, average);

                        double employmentRate = ((double) average * 1000 / population) * 100;
                        List<Double> averageAndPopulation = Arrays.asList((double) average * 1000, (double) population, employmentRate);
                        yearAveragePopulationMap.put(year, averageAndPopulation);
                    });

        } catch (IOException ex) {
            String message = ex.getMessage();
            System.out.println(message);
        }
    }


    private static Map<EmploymentData.Decade, Double> getAverageValuesByDecade(List<EmploymentData> employmentDataList) {
        // Place numbers of Workers to the List of Map
        Map<EmploymentData.Decade, List<Integer>> numbersWorkersByDecadeMap = employmentDataList.stream()
                .collect(Collectors.groupingBy(
                        EmploymentData::getDecade,
                        Collectors.mapping(
                                EmploymentData::getValue,
                                Collectors.toList()
                        )
                ));


        // Place numbers of Population to the List of Map
        Map<EmploymentData.Decade, List<Integer>> numberPopulationByDecadeMap = uSPopulationByYearMap.entrySet().stream()
                .collect(Collectors.groupingBy(e -> getDecade((e.getKey() % 100) / 10),
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));


        // Get average for workers per decade
        Map<EmploymentData.Decade, Integer> averageWorkersPerDecadeMap = numbersWorkersByDecadeMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<Integer> workersValues = entry.getValue();
                            int totalWorkers = workersValues.stream().mapToInt(Integer::intValue).sum();
                            return totalWorkers / workersValues.size() * 1000;
                        }
                ));


        // Get average for population per decade
        Map<EmploymentData.Decade, Long> averagePopulationPerDecadeMap = numberPopulationByDecadeMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            List<Integer> populationValues = e.getValue();
                            long totalPopulation = populationValues.stream().mapToLong(Integer::longValue).sum();
                            return totalPopulation / populationValues.size();
                        }
                ));


        // Get rate
        Map<EmploymentData.Decade, Double> employmentRate = averageWorkersPerDecadeMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            int averageWorkers = entry.getValue();
                            long totalPopulation = averagePopulationPerDecadeMap.getOrDefault(entry.getKey(), 1L);
                            return (totalPopulation != 0) ? ((double) averageWorkers / totalPopulation * 100) : 0;
                        }
                ));


        return employmentRate;
    }


    private static int getAverage(List<Integer> valueList) {
        return (int) valueList.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    private static int getAverageNumEmployeesPerYear(List<Integer> valueList) {
        return getAverage(valueList);
    }

    private static EmploymentData.Decade getDecade(int num) {
        EmploymentData.Decade decade;
        if (num == 8) {
            decade = EmploymentData.Decade.EIGHTIES;
        } else if (num == 9) {
            decade = EmploymentData.Decade.NINETIES;
        } else if (num == 0) {
            decade = EmploymentData.Decade.TWO_THOUSANDS;
        } else if (num == 1) {
            decade = EmploymentData.Decade.TWENTY_TENS;
        } else {
            decade = EmploymentData.Decade.TWENTY_TWENTIES;
        }
        return decade;
    }


}
