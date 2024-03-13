public class EmploymentData implements Comparable<EmploymentData> {
    private int year;
    private String month;
    private int value;
    private Decade decade;
    private int usPopulation;


    public EmploymentData(int year, String month, int value, Decade decade, int usPopulation) {
        this.year = year;
        this.month = month;
        this.value = value;
        this.decade = decade;
        this.usPopulation = usPopulation;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Decade getDecade() {
        return decade;
    }

    public void setDecade(Decade decade) {
        this.decade = decade;
    }

    public int getUsPopulation() {
        return usPopulation;
    }

    public void setUsPopulation(int usPopulation) {
        this.usPopulation = usPopulation;
    }

    public enum Decade {
        EIGHTIES("80s"),
        NINETIES("90s"),
        TWO_THOUSANDS("2000s"),
        TWENTY_TENS("2010s"),
        TWENTY_TWENTIES("2020s");

        private final String displayDecade;

        private Decade(String displayDecade) {
            this.displayDecade = displayDecade;
        }

        public String getDisplayDecade() {
            return displayDecade;
        }
    }


    @Override
    public String toString() {
        return "Employment Data: { " + "year: " + year + "," +
                " month: " + month + "," + " value: " + value + "," +
                " decade: " + decade.displayDecade +
                " population: " + usPopulation + " }";
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof EmploymentData otherEmploymentData)
                && super.equals(obj)
                && this.year == otherEmploymentData.year
                && this.month.equals(otherEmploymentData.month)
                && this.value == otherEmploymentData.value
                && this.decade.equals(otherEmploymentData.decade)
                && this.usPopulation == otherEmploymentData.usPopulation;
    }


    @Override
    public int compareTo(EmploymentData employmentData) {
        int yearComparison = Integer.compare(getYear(), employmentData.getYear());
        if (yearComparison != 0) {
            return yearComparison;
        } else {
            return this.month.compareToIgnoreCase(employmentData.month);
        }
    }


}
