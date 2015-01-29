package vossen_en_konijnen.controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.sound.sampled.*;
import javax.swing.*;

import sun.applet.Main;
import vossen_en_konijnen.model.*;
import vossen_en_konijnen.model.actor.*;
import vossen_en_konijnen.view.*;

public class Controller extends AbstractController
{
	// Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a lynx will be created in any given grid position.
    private static final double LYNX_CREATION_PROBABILITY = 0.01;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.08;
    // The probability that a lion will be created in any given grid position.
    //private static final double LION_CREATION_PROBABILITY = 0.005;
    
    private static final double HUNTER_CREATION_PROBABILITY = 0.005;
    
    private static final double ROCK_CREATION_PROBABILITY = 0.05;
    
    private static final double GRASS_CREATION_PROBABILITY = 0.14;
    
    private static final String VERSION = "Version 0.8 Beta";

    // List of animals in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JButton oneStep, hundredStep, reset, disease, start, stop;
    private JLabel stepLabel, population;
    
    private JMenuBar menubar;
    private JMenu helpMenu;
    private JMenuItem aboutItem;
    private JMenu fileMenu;
    private JMenuItem settingsItem;
    
    private ArrayList<AbstractView> views;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    
    private static boolean run;
    
    public Controller()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Controller(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        actors = new ArrayList<Actor>();
        field = new Field(depth, width);
        views = new ArrayList<AbstractView>();

        // Create a view of the state of each location in the field.
        /*this.addStepOneListener(new SimulationActionListeners());
        this.addStepHundredListener(new SimulationActionListeners());
        this.addResetListener(new SimulationActionListeners());
        */
        Color brown = new Color(169, 39, 19);
        //Color darkViolet = new Color(148, 0, 211);
        
        makeFrame(depth, width);
        setColor(Rabbit.class, Color.yellow);
        setColor(Fox.class, Color.blue);
        setColor(Lynx.class, Color.red);
        setColor(Hunter.class, Color.black);
        setColor(Rock.class, brown);
        setColor(Grass.class, Color.green);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && isViable(field); step++) {
            simulateOneStep();
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<Actor>();        
        // Let all animals act.
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors);
            if(! actor.isActive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        actors.addAll(newActors);

        showStatus(step, field);
    }
    
    public void start()
    {
        SimulatorThread thread = new SimulatorThread();
        thread.start();
    }
    
    public void stop()
    {
        run = false;
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        stats.clearHistory();
        populate();
        
        // Show the starting state in the view.
        showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    actors.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    actors.add(rabbit);
                }
                else if(rand.nextDouble() <= LYNX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lynx lynx = new Lynx(true, field, location);
                    actors.add(lynx);
                }
                else if(rand.nextDouble() <= HUNTER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hunter hunter = new Hunter(field, location);
                    actors.add(hunter);
                }
                /*         Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    actors.add(lion);
                }*/
                else if(rand.nextDouble() <= ROCK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rock rock = new Rock(field, location);
                    actors.add(rock);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    actors.add(grass);
                }
                // else leave the location empty.
            }
        }
    }
    
    class SimulationActionListeners implements ActionListener
    {
		@Override
		public void actionPerformed(ActionEvent e) {
			String s = e.getActionCommand();
			
			if(s.equals("1 step")) {simulateOneStep(); }
			if(s.equals("100 steps")) {simulate(100); }
			if(s.equals("Reset")) {reset(); ; playSound("reset.wav"); }
            if(s.equals("Disease")) {startDisease(); playSound("disease.wav"); }
            if(s.equals("About")) { showAbout(); }
            if(s.equals("Settings")) { new SliderController(); }
            if(s.equals("Start")) {start(); }
            if(s.equals("Stop")) {stop(); }
		}
    }
    public void makeFrame(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<Class, Color>();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setTitle("Fox and Rabbit Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        AbstractView fieldView = new FieldView(this, stats, height, width);
        AbstractView pieView = new PieView(this, stats, height, width);
        AbstractView lineView = new LineView(this, stats, height, width);
        AbstractView barView = new BarView(this, stats, height, width);
        
        views.add(fieldView);
        views.add(pieView);
        views.add(lineView);
        views.add(barView);
        
        Container buttonView = new JPanel();
        buttonView.setLayout(new FlowLayout());
        Container buttonViewSub = new JPanel();
        buttonViewSub.setLayout(new GridLayout(8, 1));
        
        start = new JButton("Start");
        buttonViewSub.add(start, 0);
        stop = new JButton("Stop");
        buttonViewSub.add(stop, 1);
        buttonView.add(buttonViewSub);
        buttonViewSub.add(new JLabel(""), 2);
        oneStep = new JButton("1 step");
        buttonViewSub.add(oneStep, 3);
        hundredStep = new JButton("100 steps");
        buttonViewSub.add(hundredStep, 4);
        buttonViewSub.add(new JLabel(""), 5);
        disease = new JButton("Disease");
        buttonViewSub.add(disease, 6);
        reset = new JButton("Reset");
        buttonViewSub.add(reset, 7);
        
        
        
        menubar = new JMenuBar();
        setJMenuBar(menubar);
        fileMenu = new JMenu("File");
        menubar.add(fileMenu);
        settingsItem = new JMenuItem("Settings");
        fileMenu.add(settingsItem);
        helpMenu = new JMenu("Help");
        menubar.add(helpMenu);
        aboutItem = new JMenuItem("About");
        helpMenu.add(aboutItem);

        JTabbedPane viewContainer = new JTabbedPane();
        viewContainer.addTab("fieldView", null, fieldView, "The field in wich it all dies...");
        viewContainer.addTab("pieView", null, pieView, "The chart representing all that still lifes. :)");
        viewContainer.addTab("lineView", null, lineView, "Here you see how many there are alive");
        viewContainer.addTab("barView", null, barView, "This shows some statistics");
        
        Container contents = getContentPane();
        contents.add(stepLabel, BorderLayout.NORTH);
        contents.add(viewContainer, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        contents.add(buttonView, BorderLayout.WEST);
        
        addStepOneListener(new SimulationActionListeners());
        addStepHundredListener(new SimulationActionListeners());
        addResetListener(new SimulationActionListeners());
        addDiseaseListener(new SimulationActionListeners());
        addAboutListener(new SimulationActionListeners());
        addSettingsListener(new SimulationActionListeners());
        addStartListener(new SimulationActionListeners());
        addStopListener(new SimulationActionListeners());
        pack();
        setVisible(true);
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }
    
    public void startDisease()
    {
        Color darkViolet = new Color(148, 0, 211);
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if(actor instanceof Rabbit) {
            	((Rabbit) actor).setZiekte(Randomizer.getRandomZiekte());
            }
        }
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    public Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    public void addStepOneListener(ActionListener listenForStepOne)
    {
    	oneStep.addActionListener(listenForStepOne);
    }
    
    public void addStepHundredListener(ActionListener listenForStepHundred)
    {
        hundredStep.addActionListener(listenForStepHundred);
    }
    
    public void addResetListener(ActionListener listenForStepHundred)
    {
        reset.addActionListener(listenForStepHundred);
    }
    
    public void addDiseaseListener(ActionListener listenForDisease)
    {
        disease.addActionListener(listenForDisease);
    }
    
    public void addAboutListener(ActionListener listenForAbout)
    {
    helpMenu.addActionListener(listenForAbout);
    aboutItem.addActionListener(listenForAbout);
    }
    
    public void addSettingsListener(ActionListener listenForAbout)
    {
    settingsItem.addActionListener(listenForAbout);
    }
    
    public void addStartListener(ActionListener listenForStart)
    {
        start.addActionListener(listenForStart);
    }
    
    public void addStopListener(ActionListener listenForStop)
    {
        stop.addActionListener(listenForStop);
    }
    
    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        
        Iterator it = views.iterator();
        
        while(it.hasNext()) {
        	AbstractView view = (AbstractView) it.next();
        	view.preparePaint();
        }

        it = views.iterator();
        
        while(it.hasNext()) {
        	AbstractView view = (AbstractView) it.next();
	        if(view instanceof FieldView) {
	        	FieldView fieldView = (FieldView) view;
		        for(int row = 0; row < field.getDepth(); row++) {
		            for(int col = 0; col < field.getWidth(); col++) {
		                Object animal = field.getObjectAt(row, col);
		                if(animal != null) {
		                    stats.incrementCount(animal.getClass());
		                    Color c;
		                    if(animal instanceof Rabbit) {
		                    	Rabbit r = (Rabbit) animal;
		                    	if(r.getZiekte()){
		                    		c = new Color(255,95,1);
		                    	}
		                    	else { c = getColor(animal.getClass()); }
		                    }
		                    else { c =getColor(animal.getClass()); }
		                    fieldView.drawMark(col, row, c);
		                }
		                else {
		                    fieldView.drawMark(col, row, EMPTY_COLOR);
		                }
		            }
		        }
		        stats.countFinished();
		        stats.addHistory();
	        }
	        else {
	        	view.paintChart();
	        }
        }

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        
        it = views.iterator();
        
        while(it.hasNext()) {
        	AbstractView view = (AbstractView) it.next();
        	view.repaint();
        }
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    public void showAbout()
    {
	    JOptionPane.showMessageDialog(this,
	    "Fox and Rabbit Simulation\n" + VERSION,
	    "About Fox and Rabbit Simulation",
	    JOptionPane.INFORMATION_MESSAGE
	    );
    }
    
    public static synchronized void playSound(final String url)
    {
		new Thread(new Runnable() {
			public void run()
			{
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(
					Main.class.getResourceAsStream("../../sounds/" + url));
			        clip.open(inputStream);
			        clip.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
    
    class SimulatorThread extends Thread {
         SimulatorThread() {
         }

         public void run() {
            run = true;
            while(run) {
                simulateOneStep();
                try {
                    sleep(150);
                }
                catch (InterruptedException e){}
            }
         }
     }
    
}