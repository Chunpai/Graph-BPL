package edu.albany.cs.testGraphBPL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;

import edu.albany.cs.base.APDMInputFormat;
import edu.albany.cs.base.Constants;
import edu.albany.cs.base.Stat;
import edu.albany.cs.graphGHTP.GraphGHTP;
import edu.albany.cs.scoreFuncs.FuncType;
import edu.albany.cs.scoreFuncs.Function;
import edu.albany.cs.scoreFuncs.ScoreFuncFactory;

public class TestGraphBPLONTraffic {
	private final int numOfThreads;

	private final String resultFileName = Constants.TrafficOutputFolder + "graph_GHTP_Traffic_Result.txt";
	
	private String[] testingDates = new String[] { "2014-03-01", "2014-03-02", "2014-03-03", "2014-03-04", "2014-03-05",
			"2014-03-06", "2014-03-07", "2014-03-08", "2014-03-09", "2014-03-10", "2014-03-11", "2014-03-12",
			"2014-03-13", "2014-03-14", "2014-03-15", "2014-03-16", "2014-03-17", "2014-03-18", "2014-03-19",
			"2014-03-20", "2014-03-21", "2014-03-22", "2014-03-23", "2014-03-24", "2014-03-25", "2014-03-26",
			"2014-03-27", "2014-03-28", "2014-03-29", "2014-03-30", "2014-03-31", };

	private int verboseLevel = 0;

	public TestGraphBPLONTraffic(int numOfThreads) {
		testingDates = new String[] {"2014-03-01"};
		this.numOfThreads = numOfThreads;
		run();
	}

	private void run() {
		String[] subFolders = new String[testingDates.length];
		for (int i = 0; i < testingDates.length; i++) {
			subFolders[i] = Constants.TrafficDataFolder + testingDates[i];
		}
		for (int i = 0; i < testingDates.length; i++) {
			File[] allFilesInSubFolder = new File(subFolders[i]).listFiles();
			//used for parallelism 
			final CountDownLatch latch = new CountDownLatch(allFilesInSubFolder.length);
			ExecutorService pool = Executors.newFixedThreadPool(numOfThreads);
			for (final File apdmFile : allFilesInSubFolder) {
				final APDMInputFormat apdm = new APDMInputFormat(apdmFile);
				final int graphSize = apdm.data.numNodes;
				final ArrayList<Integer[]> edges = apdm.data.intEdges;
				final double[] speeds = apdm.data.speed;
				final ArrayList<Double> edgeCosts = apdm.data.identityEdgeCosts;
				
				pool.execute(new Thread() {
					public void run() {
						//System.out.println(apdm.data.numNodes);
						//System.out.println(apdm.data.numEdges);
						//System.out.println(apdm.data.graphWeightedAdjList.get(0).size());
						
						System.out.println(edges.size());
					}
				});
			}
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pool.shutdown();
		}
	}

	public static void main(String args[]) {		
		Constants.intializeProject();
		if (args == null || args.length == 0) {
			int numOfThreads = 1;
			new TestGraphBPLONTraffic(numOfThreads);
		} else {
			int numOfThreads = Integer.parseInt(args[0]);
			new TestGraphBPLONTraffic(numOfThreads);
		}
	}

}
