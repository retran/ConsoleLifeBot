package me.retran.consolelifebot;

public class GiantBombResponse<T> {
    private T results = null;

    private int number_of_total_results = 0;

    public T results() {
        return results;
    }

    public int total() {
        return number_of_total_results;
    }
}
