package dna.datasets;

import java.io.File;

import argList.ArgList;
import argList.types.array.StringArrayArg;
import argList.types.atomic.EnumArg;
import argList.types.atomic.IntArg;
import argList.types.atomic.LongArg;
import argList.types.atomic.StringArg;
import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.io.BatchWriter;
import dna.io.GraphWriter;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.batch.BatchSanitizationStats;
import dna.updates.generators.BatchGenerator;
import dna.util.Rand;
import dna.util.fromArgs.BatchGeneratorFromArgs;
import dna.util.fromArgs.BatchGeneratorFromArgs.BatchType;
import dna.util.fromArgs.GraphDataStructuresFromArgs;
import dna.util.fromArgs.GraphDataStructuresFromArgs.GdsType;
import dna.util.fromArgs.GraphGeneratorFromArgs;
import dna.util.fromArgs.GraphGeneratorFromArgs.GraphType;

public class Dataset {

	public GdsType gdsType;
	public String[] gdsArguments;

	public GraphType graphType;
	public String[] graphArguments;

	public BatchType batchType;
	public String[] batchArguments;

	public long seed;
	public int batches;

	public String destDir;
	public String graphFilename;
	public String batchSuffix;

	public Dataset(String gdsType, String[] gdsArguments, String graphType,
			String[] graphArguments, String batchType, String[] batchArguments,
			Long seed, Integer batches, String destDir, String graphFilename,
			String batchSuffix) {
		this.gdsType = GdsType.valueOf(gdsType);
		this.gdsArguments = gdsArguments;

		this.graphType = GraphType.valueOf(graphType);
		this.graphArguments = graphArguments;

		this.batchType = BatchType.valueOf(batchType);
		this.batchArguments = batchArguments;

		this.seed = seed;
		this.batches = batches;

		this.destDir = destDir;
		this.graphFilename = graphFilename;
		this.batchSuffix = batchSuffix;
	}

	public static void main(String[] args) throws Exception {
		ArgList<Dataset> argList = new ArgList<Dataset>(
				Dataset.class,
				new EnumArg("gdsType", "type of gds to use", GdsType.values()),
				new StringArrayArg("gdsArguments", "arguments for the gds", ","),
				new EnumArg("graphType", "type of graph to generate", GraphType
						.values()), new StringArrayArg("graphArguments",
						"arguments for the graph generator", ","), new EnumArg(
						"batchType", "type of batches to generate", BatchType
								.values()), new StringArrayArg(
						"batchArguments", "arguments for the batch generator",
						","), new LongArg("seed",
						"seed to initialize the PRNG with"), new IntArg(
						"batches", "number of batches to generate"),
				new StringArg("destDir", "dir where to store the dataset"),
				new StringArg("graphFilename", "e.g., 0.dnag"), new StringArg(
						"batchSuffix", "e.g., .dnab"));

		// args = new String[] { "Undirected", "", "Random", "20,30", "Random",
		// "0,0,2,1", "0", "10", "../examples/random/", "0.dnag", ".dnab" };

		Dataset d = argList.getInstance(args);
		d.generate();
	}

	public void generate() throws Exception {
		if (!((new File(destDir)).exists())) {
			(new File(destDir)).mkdirs();
		}

		Rand.seed = seed;
		Rand.init(seed);

		GraphDataStructure gds = GraphDataStructuresFromArgs.parse(gdsType,
				gdsArguments);
		GraphGenerator gg = GraphGeneratorFromArgs.parse(gds, graphType,
				graphArguments);
		BatchGenerator bg = BatchGeneratorFromArgs.parse(gg, batchType,
				batchArguments);

		Graph g = gg.generate();
		System.out.println(g + " => " + destDir + graphFilename);
		GraphWriter.write(g, destDir, graphFilename);

		for (int i = 0; i < this.batches; i++) {
			if (!bg.isFurtherBatchPossible(g)) {
				break;
			}
			Batch b = bg.generate(g);
			BatchSanitizationStats bss = BatchSanitization.sanitize(b);
			if (bss.getTotal() > 0) {
				System.out.println(bss);
			}
			System.out.println(b + " => " + destDir + b.getTo() + batchSuffix);
			BatchWriter.write(b, destDir, b.getTo() + batchSuffix);
			b.apply(g);
		}
	}

}
