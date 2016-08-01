package dna.datasets;

import java.io.File;

import argList.ArgList;
import argList.types.atomic.BooleanArg;
import argList.types.atomic.EnumArg;
import argList.types.atomic.IntArg;
import argList.types.atomic.StringArg;
import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.util.ReadableFileGraph;
import dna.io.EdgeListGraphWriter;
import dna.io.EdgeListGraphWriter.InfoType;
import dna.io.NodeListGraphWriter;
import dna.updates.batch.Batch;
import dna.updates.generators.BatchGenerator;
import dna.updates.generators.util.ReadableDirConsecutiveBatchGenerator;

public class SnapshotExport {

	public String srcDir;
	public String graphFilename;
	public String batchSuffix;
	public int batches;

	public String dstDir;
	public String snapshotSuffix;

	public OutputFormat outputFormat;
	public InfoType infoType;
	public String separator;
	public boolean addInverseEdge;
	public boolean incrementIndex;

	public enum OutputFormat {
		EDGE_LIST, NODE_LIST
	};

	public enum SeparatorType {
		COMMA, TAB
	};

	public SnapshotExport(String srcDir, String graphFilename,
			String batchSuffix, Integer batches, String dstDir,
			String fileSuffix, String outputFormat, String infoType,
			String separatorType, Boolean addInverseEdge, Boolean incrementIndex) {
		this.srcDir = srcDir;
		this.graphFilename = graphFilename;
		this.batchSuffix = batchSuffix;
		this.batches = batches;

		this.dstDir = dstDir;
		this.snapshotSuffix = fileSuffix;

		this.outputFormat = OutputFormat.valueOf(outputFormat);
		this.infoType = InfoType.valueOf(infoType);
		switch (SeparatorType.valueOf(separatorType)) {
		case COMMA:
			this.separator = ",";
			break;
		case TAB:
			this.separator = "	";
			break;
		default:
			throw new IllegalArgumentException("invalid separator type: "
					+ separator);
		}
		this.addInverseEdge = addInverseEdge;
		this.incrementIndex = incrementIndex;
	}

	public static void main(String[] args) throws Exception {
		ArgList<SnapshotExport> argList = new ArgList<SnapshotExport>(
				SnapshotExport.class,
				new StringArg("srcDir", "path to DNA dataset, ending with '/'"),
				new StringArg("graphFilename",
						"filename of initial graph, e.g., 0.dnag"),
				new StringArg("batchSuffix",
						"suffix of batch files, e.g., .dnab"),
				new IntArg("batches", "number of batches to process"),
				new StringArg("dstDir",
						"path where to write files, ending with '/'"),
				new StringArg("snapshotSuffix", "suffix of files to be written"),
				new EnumArg("outputFormat", "format of the output",
						OutputFormat.values()),
				new EnumArg("infoType",
						"what to put at the beginning of each snapshot file",
						InfoType.values()),
				new EnumArg("separatorType", "separator used for each edge",
						SeparatorType.values()),
				new BooleanArg("addInverseEdge",
						"if set true, undirected edges {a,b} are output as (a,b) AND (b,a)"),
				new BooleanArg(
						"incrementIndex",
						"if set true, each node index ist inremented by 1 (in case node indexes must start at 1)"));

		SnapshotExport export = argList.getInstance(args);
		export.export();
	}

	public void export() throws Exception {
		GraphGenerator gg = new ReadableFileGraph(this.srcDir,
				this.graphFilename);
		BatchGenerator bg = new ReadableDirConsecutiveBatchGenerator("BG",
				this.srcDir, this.batchSuffix);

		(new File(this.dstDir)).mkdirs();

		Graph g = gg.generate();
		this.write(g);
		for (int i = 0; i < this.batches; i++) {
			Batch b = bg.generate(g);
			b.apply(g);
			this.write(g);
		}
	}

	protected boolean write(Graph g) throws Exception {
		String filename = g.getTimestamp() + this.snapshotSuffix;
		System.out.println("writing " + this.dstDir + filename);
		switch (this.outputFormat) {
		case EDGE_LIST:
			return EdgeListGraphWriter.write(g, this.dstDir, filename,
					this.separator, this.infoType, addInverseEdge,
					this.incrementIndex, "", "");
		case NODE_LIST:
			return NodeListGraphWriter.write(g, dstDir, filename,
					this.incrementIndex);
		default:
			throw new IllegalArgumentException("invalid output format: "
					+ this.outputFormat);
		}
	}

}
