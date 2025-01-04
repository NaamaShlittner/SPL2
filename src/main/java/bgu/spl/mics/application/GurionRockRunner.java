package bgu.spl.mics.application;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.gson_files.Config;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.services.*;

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
        
        Config c = Config.parseInputConfig("C:\\Users\\Nadav\\OneDrive\\שולחן העבודה\\לימודים\\SPL_2\\SPL2\\example input\\configuration_file.json");
        // Config c = Config.parseInputConfig("C:\\Users\\Nadav\\OneDrive\\שולחן העבודה\\לימודים\\SPL_2\\SPL2\\example_input_2\\configuration_file.json");
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
        services.add(new FusionSlamService(FusionSlam.getInstance(), new StatisticalFolder()));
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
