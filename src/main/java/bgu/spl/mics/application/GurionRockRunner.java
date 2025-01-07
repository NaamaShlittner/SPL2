package bgu.spl.mics.application;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.gson_files.Config;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.FusionSlamService;
import bgu.spl.mics.application.services.LiDarService;
import bgu.spl.mics.application.services.PoseService;
import bgu.spl.mics.application.services.TimeService;

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
    public static void main(String[] args) throws InterruptedException {
        System.out.println("System is starting...");

        // Check if the user provided the path as an argument
        if (args.length < 1) {
            System.out.println("Error: No configuration file path provided.");
            System.exit(1); // Exit the program with an error code
        }

        // Use the path from the command-line argument
        String configFilePath = args[0];
        Config c = Config.parseInputConfig(configFilePath);
        List<Camera> cameras = c.getCameras();
        List<LiDarWorkerTracker> liDarWorkerTrackers = c.getLiDarWorkers();
        int duration = c.getDuration();
        int tickTime = c.getTickTime();
        GPSIMU gpsimu = c.getGpsimu();

        List<MicroService> services = new ArrayList<MicroService>();
        for (Camera camera : cameras) {
            services.add(new CameraService(camera));
        }
        for (LiDarWorkerTracker liDarWorkerTracker : liDarWorkerTrackers) {
            services.add(new LiDarService(liDarWorkerTracker));
        }
        services.add(new FusionSlamService(FusionSlam.getInstance(), StatisticalFolder.getInstance(), Paths.get(configFilePath).getParent().toString()));
        services.add(new PoseService(gpsimu));
        MicroService timeService = new TimeService(tickTime, duration);

        for (MicroService service : services) {
            Thread t = new Thread(service);
            t.start();
        }

        try {
            Thread.sleep(1000); // wait for all services to initialize before starting the time service
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Thread timeThread = new Thread(timeService);
        timeThread.start();
        
        // TODO: Initialize system components and services.
        
        // TODO: Start the simulation.
    }
}
