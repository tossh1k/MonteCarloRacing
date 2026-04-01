import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        int[][] track = Constants.SECOND_TRACK;

        Environment env = new Environment(track);
        Agent agent = new Agent(track.length, track[0].length);

        System.out.println("Starting training...");
        agent.MonteCarlo(env, 10000000);

        Visualize.demonstrateWithVisualization(env, agent, 5);
    }
}