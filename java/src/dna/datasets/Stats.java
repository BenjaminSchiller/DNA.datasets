package dna.datasets;

import java.util.ArrayList;

import argList.ArgList;
import argList.types.atomic.StringArg;
import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.util.ReadableFileGraph;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.util.ReadableDirConsecutiveBatchGenerator;

public class Stats {

	public String datasetDir;
	public String graphFilename;
	public String batchSuffix;

	public Stats(String datasetDir, String graphFilename, String batchSuffix) {
		this.datasetDir = datasetDir;
		this.graphFilename = graphFilename;
		this.batchSuffix = batchSuffix;
	}

	@SuppressWarnings("unchecked")
	public void printStats() throws Exception {
		GraphGenerator gg = new ReadableFileGraph(this.datasetDir,
				this.graphFilename);
		BatchGenerator bg = new ReadableDirConsecutiveBatchGenerator("bg",
				this.datasetDir, this.batchSuffix);

		printStats(prefixNames + prefixGraph, getNamesG());
		printStats(prefixNames + prefixBatch, getNamesB());

		Graph g = gg.generate();
		printStats(prefixGraph, getStats(g));

		while (bg.isFurtherBatchPossible(g)) {
			Batch b = bg.generate(g);
			b.apply(g);
			printStats(prefixGraph, getStats(g));
			printStats(prefixBatch, getStats(b));
		}
	}

	public static final String sep = "	";

	public static final String prefixNames = "#";
	public static final String prefixGraph = "G";
	public static final String prefixBatch = "B";

	@SuppressWarnings("unchecked")
	protected static void printStats(String prefix, ArrayList<String>... lists) {
		StringBuffer buff = new StringBuffer(prefix);
		for (ArrayList<String> list : lists) {
			for (String str : list) {
				buff.append(sep + str);
			}
		}
		System.out.println(buff.toString());
	}

	protected static ArrayList<String> getNamesG() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("TIMESTAMP");
		list.add("NODES");
		list.add("EDGES");
		return list;
	}

	protected static ArrayList<String> getStats(Graph g) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(g.getTimestamp() + "");
		list.add(g.getNodeCount() + "");
		list.add(g.getEdgeCount() + "");
		return list;
	}

	protected static ArrayList<String> getNamesB() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("FROM");
		list.add("TO");
		list.add("NA");
		list.add("NR");
		list.add("NW");
		list.add("EA");
		list.add("ER");
		list.add("EW");
		return list;
	}

	protected static ArrayList<String> getStats(Batch b) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(b.getFrom() + "");
		list.add(b.getTo() + "");
		list.add(b.getNodeAdditionsCount() + "");
		list.add(b.getNodeRemovalsCount() + "");
		list.add(b.getNodeWeightsCount() + "");
		list.add(b.getEdgeAdditionsCount() + "");
		list.add(b.getEdgeRemovalsCount() + "");
		list.add(b.getEdgeWeightsCount() + "");
		return list;
	}

	public static void main(String[] args) throws Exception {
		ArgList<Stats> argList = new ArgList<Stats>(Stats.class, new StringArg(
				"datasetDir", "dir where the dataset is stored"),
				new StringArg("graphFilename", "e.g., 0.dnag"), new StringArg(
						"batchSuffix", "e.g., .dnab"));

		Stats stats = argList.getInstance(args);
		stats.printStats();
	}

}
