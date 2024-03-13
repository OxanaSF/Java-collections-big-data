import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class EmploymentDataDriver {
    private static Map<Integer, Integer> uSPopulationByYearMap;

    public static void main(String[] args) {

        uSPopulationByYearMap = new HashMap<>();
        fillMapWithUSPopulationData(uSPopulationByYearMap);
//        System.out.println();
//        System.out.println("The U.S. Population: " + uSPopulationByYearMap.keySet().size());
//        printUSPopulationByYear(uSPopulationByYearMap);


        List<EmploymentData> employmentDataList = new ArrayList<>();
        Map<Integer, List<Integer>> numberOfEmployeesByYearMap = new HashMap<>();
        Map<EmploymentData.Decade, List<EmploymentData>> employmentDataByDecadeMap = new HashMap<>();
        Map<Integer, Integer> averageNumEMployeesPerYearMap = new HashMap<>();
        Map<Integer, List<Double>> yearAveragePopulationMap = new HashMap<>();

        fillListAndMapWithEmploymentData(employmentDataList, numberOfEmployeesByYearMap,
                employmentDataByDecadeMap, averageNumEMployeesPerYearMap, yearAveragePopulationMap);

//        System.out.println("Total # Employment Data: " + employmentDataList.size());
//        printAllObjects(employmentDataList);
//
//        System.out.println();
//        System.out.println("Total # of Years with employment data: " + numberOfEmployeesByYearMap.keySet().size());
//        printEmployeesByMonthAndYear(numberOfEmployeesByYearMap);
//
//        System.out.println();
//        System.out.println("Employment Data by Decade: " + employmentDataByDecadeMap.keySet().size());
//        printEmployeesByDecade(employmentDataByDecadeMap);
//
//        System.out.println();
//        System.out.println("Average Number of Employees per Year: " + averageNumEMployeesPerYearMap.keySet().size());
//        printAvarageNumEmployeesPerYear(averageNumEMployeesPerYearMap);
//
//
//        System.out.println();
//        System.out.println("Average Number of Employees and Population of the Year: " + yearAveragePopulationMap.keySet().size());
//        printYearAveragePopulationMap(yearAveragePopulationMap);


        // QUESTION 1: What is three lowest employment rates in the 1980 - 2024 period
        // considering the U.S. population data for those years.
        EmploymentRateComparator employmentRateComparator = new EmploymentRateComparator();
        List<Map.Entry<Integer, List<Double>>> entryList = new ArrayList<>(yearAveragePopulationMap.entrySet());
        entryList.sort(employmentRateComparator);
        System.out.println("Three Lowest Employment Rates in the 1980 - 2024 Period:");
        printThreeLowestEmploymentRate(entryList);


        System.out.println();

        // QUESTION 2: What is the average employment rate for each decade from the 1980s to the 2020s?
        Map<EmploymentData.Decade, Double> averageEmploymentRateByDecade = getAverageValuesByDecade(employmentDataList);
        List<Map.Entry<EmploymentData.Decade, Double>> employmentRateEntryList = new ArrayList<>(averageEmploymentRateByDecade.entrySet());
        System.out.println("Average Employment Rate for Each Decade From the 1980s to the 2020s:");
        printAverageEmploymentRateByDecade(averageEmploymentRateByDecade);


    }

    public static class EmploymentRateComparator implements Comparator<Map.Entry<Integer, List<Double>>> {
        @Override
        public int compare(Map.Entry<Integer, List<Double>> entry1, Map.Entry<Integer, List<Double>> entry2) {
            return Double.compare(entry1.getValue().get(2), entry2.getValue().get(2));
        }
    }


    private static void fillMapWithUSPopulationData(Map<Integer, Integer> uSPopulationByYearMap) {
        try (Scanner fileScan = new Scanner(new FileReader(new File("united-states-population-2024-03-10.csv")))) {
            fileScan.nextLine();
            while (fileScan.hasNextLine()) {
                String line = fileScan.nextLine();
                String[] data = line.split(",");
                int year = Integer.parseInt(data[0].substring(0, 4));
                int population = Integer.parseInt(data[1]);
                uSPopulationByYearMap.put(year, population);
            }
        } catch (IOException ex) {
            String message = ex.getMessage();
            System.out.println(message);
        }
    }


    private static void fillListAndMapWithEmploymentData(List<EmploymentData> employmentDataList,
                                                         Map<Integer, List<Integer>> numberOfEmployeesByYearMap,
                                                         Map<EmploymentData.Decade, List<EmploymentData>> employmentRateByDecadeMap,
                                                         Map<Integer, Integer> avarageNumEMployeesPerYearMap,
                                                         Map<Integer, List<Double>> yearAveragePopulationMap) {

        try (Scanner fileScan = new Scanner(new FileReader(new File("total-employees-1980-2024.csv")))) {

            fileScan.nextLine();

            while (fileScan.hasNextLine()) {
                String line = fileScan.nextLine();
                String[] data = line.split(",");

                int year = Integer.parseInt(data[1]);
                String month = data[2];
                int value = Integer.parseInt(data[4]);

                int thirdDigit = (year % 100) / 10;

                EmploymentData.Decade decade = getDecade(thirdDigit);

                Integer population = uSPopulationByYearMap.get(year);

                EmploymentData employmentData = new EmploymentData(year, month, value, decade, population);

                employmentDataList.add(employmentData);

                List<Integer> valueList = new ArrayList<>();
                if (numberOfEmployeesByYearMap.containsKey(year)) {
                    numberOfEmployeesByYearMap.get(year).add(value);
                } else {
                    valueList.add(value);
                    numberOfEmployeesByYearMap.put(year, valueList);
                }


                if (employmentRateByDecadeMap.containsKey(decade)) {
                    employmentRateByDecadeMap.get(decade).add(employmentData);
                } else {
                    List<EmploymentData> decadeList = new ArrayList<>();
                    decadeList.add(employmentData);
                    employmentRateByDecadeMap.put(decade, decadeList);
                }


                int average = getAvarageNumEmployeesPerYear(numberOfEmployeesByYearMap.get(year));

                avarageNumEMployeesPerYearMap.put(year, average);


                List<Double> averageAndPopulation = new ArrayList<>();
                double employmentRate = ((double) average * 1000 / population) * 100;
                averageAndPopulation.add((double) average * 1000);
                averageAndPopulation.add((double) population);
                averageAndPopulation.add(employmentRate);
                yearAveragePopulationMap.put(year, averageAndPopulation);


            }

        } catch (IOException ex) {
            String message = ex.getMessage();
            System.out.println(message);
        }
    }


    private static Map<EmploymentData.Decade, Double> getAverageValuesByDecade(List<EmploymentData> employmentDataList) {

        // Place numbers of Workers to the List of Map
        Map<EmploymentData.Decade, List<Integer>> numbersWorkersByDecadeMap = new HashMap<>();
        for (EmploymentData employmentData : employmentDataList) {
            EmploymentData.Decade decade = employmentData.getDecade();
            int numOfWorkers = employmentData.getValue();
            if (numbersWorkersByDecadeMap.containsKey(decade)) {
                numbersWorkersByDecadeMap.get(decade).add(numOfWorkers);
            } else {
                List<Integer> values = new ArrayList<>();
                values.add(numOfWorkers);
                numbersWorkersByDecadeMap.put(decade, values);
            }

        }


        // Place numbers of Population to the List of Map
        Map<EmploymentData.Decade, List<Integer>> numberPopulationByDecadeMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : uSPopulationByYearMap.entrySet()) {
            Integer year = entry.getKey();
            int thirdDigit = (year % 100) / 10;
            EmploymentData.Decade decade = getDecade(thirdDigit);
            Integer population = entry.getValue();
            if (numberPopulationByDecadeMap.containsKey(decade)) {
                numberPopulationByDecadeMap.get(decade).add(population);
            } else {
                List<Integer> values = new ArrayList<>();
                values.add(population);
                numberPopulationByDecadeMap.put(decade, values);
            }
        }


        // Get sum for workers per decade
        Map<EmploymentData.Decade, Integer> averageWorkersPerDecadeMap = new HashMap<>();
        for (Map.Entry<EmploymentData.Decade, List<Integer>> entry : numbersWorkersByDecadeMap.entrySet()) {
            EmploymentData.Decade decade = entry.getKey();
            List<Integer> workersValues = entry.getValue();
            int totalWorkers = (int) sum(workersValues);
            int averageWorkers = (totalWorkers / workersValues.size()) * 1000;
            averageWorkersPerDecadeMap.put(decade, averageWorkers);
        }


        // Get sum for population per decade
        Map<EmploymentData.Decade, Long> averagePopulationPerDecadeMap = new HashMap<>();
        for (Map.Entry<EmploymentData.Decade, List<Integer>> entry : numberPopulationByDecadeMap.entrySet()) {
            EmploymentData.Decade decade = entry.getKey();
            List<Integer> populationValues = entry.getValue();
            long totalPopulation = 0;
            for (int num : populationValues) {
                totalPopulation += num;
            }
            long averagePopulation = totalPopulation / populationValues.size();
            averagePopulationPerDecadeMap.put(decade, averagePopulation);
        }

        // Get rate
        Map<EmploymentData.Decade, Double> employmentRate = new HashMap<>();
        for (Map.Entry<EmploymentData.Decade, Integer> entry : averageWorkersPerDecadeMap.entrySet()) {
            EmploymentData.Decade decade = entry.getKey();
            int totalWorkers = entry.getValue();
            long totalPopulation = averagePopulationPerDecadeMap.get(decade);

            double rate = ((double) totalWorkers / totalPopulation) * 100;
            employmentRate.put(decade, rate);
        }


        return employmentRate;
    }

    public static long sum(List<Integer> list) {
        int sum = 0;
        for (int num : list) {
            sum += num;
        }
        return sum;
    }


    private static int getAverage(List<Integer> valueList) {
        if (valueList.isEmpty()) return 0;
        int sum = 0;
        for (int val : valueList) {
            sum += val;
        }
        int average = sum / valueList.size();
        return average;
    }

    private static int getAvarageNumEmployeesPerYear(List<Integer> valueList) {
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


    private static void printThreeLowestEmploymentRate(List<Map.Entry<Integer, List<Double>>> entryList) {
        for (int i = 0; i < 3 && i < entryList.size(); i++) {
            Map.Entry<Integer, List<Double>> entry = entryList.get(i);
            double employmentRate = entry.getValue().get(2);
            int year = entry.getKey();
            System.out.printf("Year: %d, Employment Rate: %.2f%%\n", year, employmentRate);
        }
    }

    private static void printAverageEmploymentRateByDecade(Map<EmploymentData.Decade, Double> averageValuesByDecadeMap) {
        for (Map.Entry<EmploymentData.Decade, Double> entry : averageValuesByDecadeMap.entrySet()) {
            EmploymentData.Decade decade = entry.getKey();
            double rate = entry.getValue();
            String formattedRate = String.format("%.2f%%", rate);
            System.out.println("Decade: " + decade.getDisplayDecade() + ", Employment Rate: " + formattedRate);
        }
    }


    private static void printAllObjects(List<EmploymentData> employmentDataList) {
        for (EmploymentData obj : employmentDataList) {
            System.out.println(obj.toString());
        }
    }

    private static void printEmployeesByMonthAndYear(Map<Integer, List<Integer>> employmentRateByYearMap) {
        for (Map.Entry<Integer, List<Integer>> entry : employmentRateByYearMap.entrySet()) {
            int year = entry.getKey();
            List<Integer> values = entry.getValue();
            System.out.print("Year: " + year + ", Values: ");
            for (Integer value : values) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    private static void printEmployeesByDecade(Map<EmploymentData.Decade, List<EmploymentData>> employmentRateByDecadeMap) {
        for (Map.Entry<EmploymentData.Decade, List<EmploymentData>> entry : employmentRateByDecadeMap.entrySet()) {
            EmploymentData.Decade decade = entry.getKey();
            List<EmploymentData> values = entry.getValue();
            System.out.println("Decade: " + decade.getDisplayDecade());
            for (EmploymentData value : values) {
                System.out.println(value.toString());
            }
            System.out.println();
        }
    }

    private static void printAvarageNumEmployeesPerYear(Map<Integer, Integer> avarageNumEMployeesPerYearMap) {
        for (Map.Entry<Integer, Integer> entry : avarageNumEMployeesPerYearMap.entrySet()) {
            System.out.println("Year: " + entry.getKey() + ", Average: " + entry.getValue());
        }
    }


    private static void printUSPopulationByYear(Map<Integer, Integer> uSPopulationByYearMap) {
        for (Map.Entry<Integer, Integer> entry : uSPopulationByYearMap.entrySet()) {
            System.out.println("Year: " + entry.getKey() + ", Population: " + entry.getValue());
        }
    }

    private static void printYearAveragePopulationMap(Map<Integer, List<Double>> yearAveragePopulationMap) {
        for (Map.Entry<Integer, List<Double>> entry : yearAveragePopulationMap.entrySet()) {
            double averageEmployees = (entry.getValue().get(0)) / 1e6;
            double populationMillions = entry.getValue().get(1) / 1e6;
            double employmentRate = entry.getValue().get(2);
            String formattedEmploymentRate = String.format("%.2f%%", employmentRate);
            System.out.println("Year: " + entry.getKey() +
                    ", Average Employees per year: " + averageEmployees + " millions" +
                    ", Population at the year: " + populationMillions + " millions" +
                    ", Employment Rate: " + formattedEmploymentRate);
        }
    }


}
