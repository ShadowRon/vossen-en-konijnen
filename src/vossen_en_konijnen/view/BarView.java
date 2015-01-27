package vossen_en_konijnen.view;

import java.util.*;
import java.awt.*;

import vossen_en_konijnen.controller.Controller;
import vossen_en_konijnen.model.Counter;
import vossen_en_konijnen.model.FieldStats;

@SuppressWarnings({"serial","rawtypes","unused"})
public class BarView extends AbstractView
{

	public BarView(Controller model, FieldStats stats, int height, int width) 
	{
		super(model, stats, height, width);
	}
	
	public void updateStats(FieldStats stats)
	{
		this.stats = stats;
	}
	
	 /**
     * Prepare for a new round of painting. Since the component
     * may be resized, compute the scaling factor again.
     */
    public void preparePaint()
    {
        if(! size.equals(getSize())) {  // if the size has changed...
            size = getSize();
            fieldImage = this.createImage(size.width, size.height);
            g = fieldImage.getGraphics();

            xScale = size.width / gridWidth;
            if(xScale < 1) {
                xScale = GRID_VIEW_SCALING_FACTOR;
            }
            yScale = size.height / gridHeight;
            if(yScale < 1) {
                yScale = GRID_VIEW_SCALING_FACTOR;
            }
        }
    }
	
    /**
     * 
     */
	public void paintChart() {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, size.width, size.height);
		
		int leftLine = size.width/20; // 5% = x/(100/5)
		int bottomLine = size.height - size.height/100;
		int maxHeight = size.height / 40; // 2.5% = x/(100/2.5) 
		
		g.setColor(Color.BLACK);
		g.drawLine(leftLine,0,leftLine,bottomLine);
		g.drawLine(leftLine, bottomLine, size.width, bottomLine);
		
		HashMap<Class, Counter> counters = stats.getCounters();
		int fullFill = 0;
		int actorCount = 0;
		
		for(Counter value : counters.values()) { fullFill += value.getCount(); actorCount++; }
		
		int barWidth = (size.width - leftLine) / ((actorCount * 2) + 1);
		double heightScale = ((float) bottomLine - maxHeight) / fullFill;
		
		int x = 1;
		
		for(Class key : counters.keySet()) {
            int count = counters.get(key).getCount();
            int height = (int) (count * heightScale);
            g.setColor(model.getColor(key));
            int x1 = leftLine + barWidth*(2*x-1);
            
            int t1 = (bottomLine + height);
            int t2 = size.height - t1;
            int t3 = size.height - t2;
            
            int y1 = bottomLine - height;
            int width = barWidth;
    		g.fillRect(x1, y1, width, height);
    		x++;
        }
		
		
	}
	
	public void paintComponent(Graphics g) 
	{
		if(fieldImage != null) {
            Dimension currentSize = getSize();
            if(size.equals(currentSize)) {
                g.drawImage(fieldImage, 0, 0, null);
            }
            else {
                // Rescale the previous image.
                g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
            }
        }
	}	
}