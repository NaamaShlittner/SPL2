package bgu.spl.mics.application;

import bgu.spl.mics.application.gson_files.Config;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        
        Config c = Config.parseInputConfig("C:\\Users\\Nadav\\OneDrive\\שולחן העבודה\\לימודים\\SPL_2\\SPL2\\example input\\configuration_file.json");
        c.getLiDarWorkers().get(0).printDataBase();
        // Config.parseInputConfig("C:\\Users\\Nadav\\OneDrive\\שולחן העבודה\\לימודים\\SPL_2\\SPL2\\example_input_2\\configuration_file.json");
        
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }
}
