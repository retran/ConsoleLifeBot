package me.retran.consolelifebot.giantbomb;

public class GiantBombResponse<T> {
	private T results;
	
	private int number_of_total_results;
	
	public T results() {
		return results;
	}
	
	public int total() {
		return number_of_total_results;
	}
}
