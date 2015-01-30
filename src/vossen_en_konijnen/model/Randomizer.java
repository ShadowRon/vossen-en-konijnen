package vossen_en_konijnen.model; 

import java.util.Random;

/**
 * Provide control over the randomization of the simulation.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class Randomizer
{
    // The default seed for control of randomization.
    private static final int SEED = 1111;
    // A shared Random object, if required.
    private static final Random rand = new Random(SEED);
    // Determine whether a shared random generator is to be provided.
    private static final boolean useShared = true;

    /**
     * Constructor for objects of class Randomizer
     */
    public Randomizer()
    {
    }

    /**
     * Provide a random generator.
     * @return A random object.
     */
    public static Random getRandom()
    {
        if(useShared) {
            return rand;
        }
        else {
            return new Random();
        }
    }
    
    /**
     * Reset the randomization.
     * This will have no effect if randomization is not through
     * a shared Random generator.
     */
    public static void reset()
    {
        if(useShared) {
            rand.setSeed(SEED);
        }
    }
    
    /**
     * Determine if a rabbit has a disease gen
     * @return if a rabbit has a disease gen
     */
    public static boolean getRandomZiekteGen() 
    {
        boolean ziekte = true;
        int range = (10 - 1) + 1;     
        int value = (int)(Math.random() * range) + 1;
        if(value > 1) {
            ziekte = true;
        }
        else if(value == 1) {
            ziekte = false;
        }
        return ziekte;
    }
    
    /**
     * Determine if a rabbit gets sick
     * @return Will the rabbit get sick when disease got released?
     */
    public static boolean getRandomZiekte()
    {
    	boolean ziekte = false;
        int range = (10 - 1) + 1;     
        int value = (int)(Math.random() * range) + 1;
        if(value > 1) {
            ziekte = false;
        }
        else if(value == 1) {
            ziekte = true;
        }
        return ziekte;
    }
}
