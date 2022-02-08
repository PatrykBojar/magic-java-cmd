package main;

/**
 * Simulation of a command console to perform actions with customised
 * instructions. Starts the Console.
 * 
 * @author Patryk Bojar
 * @version 1.0.0, 29 Nov 2021
 */
public class ConsoleApp {
	/**
	 * This is the main method which makes use of ConsolePretty.showStartMessage and
	 * Console.askForCommand() method. Starts the console.
	 * 
	 * @param args unused.
	 * @throws InterruptedException a exception if thread is interrupted.
	 */
	public static void main(String[] args) throws InterruptedException {
		ConsolePretty.showStartMessage(); // shows the initial message (on every start-up).
		Console.askForCommand(); // starts the console
	}

}