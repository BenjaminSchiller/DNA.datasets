# DNA.datasets

This repo provides sources for creating various datasets of dynamic graphs using the [Dynamic Network Analyzer (DNA)](https://github.com/BenjaminSchiller/DNA) framework.
The generated files are written in the DNA format as specified [here](https://github.com/BenjaminSchiller/DNA.doc/blob/master/doc/FORMATS.md).


## Usage

To generate a dataset, use the following command:

	java -jar dataset.jar $gdsType $gdsArguments $graphType $graphArguments $batchType $batchArguments $seed $batches $destDir $graphFilename $batchSuffix

It is mandatory to specify all 11 arguments.
They are described in the following:

	expecting 11 arguments (got 0)
	   0: gdsType - type of gds to use (String)
	      values:  Directed DirectedV DirectedE DirectedVE Undirected UndirectedV UndirectedE UndirectedVE
	
	   1: gdsArguments - arguments for the gds (String[]) sep. by ','
	
	   2: graphType - type of graph to generate (String)
	      values:  Empty Clique Grid2d Grid3d HoneyComb Ring RingStar Star Random BarabasiAlbert PositiveFeedbackPreference ReadableEdgeListFileGraph Timestamped Konect
	
	   3: graphArguments - arguments for the graph generator (String[]) sep. by ','
	
	   4: batchType - type of batches to generate (String)
	      values:  BarabasiAlbert PositiveFeedbackPreference RandomGrowth RandomScaling Random RandomW RandomEdgeExchange Timestamped Konect
	
	   5: batchArguments - arguments for the batch generator (String[]) sep. by ','
	
	   6: seed - seed to initialize the PRNG with (Long)
	
	   7: batches - number of batches to generate (Integer)
	
	   8: destDir - dir where to store the dataset (String)
	
	   9: graphFilename - e.g., 0.dnag (String)
	
	  10: batchSuffix - e.g., .dnab (String)

For each component (gdsType, graphType, batchType) arguments can be specified to describe the parameters of the respective type (gdsArgument, graphArguments batchArguments).
For a list of possible arguments for the different types, we refer to [this (most probably not complete) documentation](https://github.com/BenjaminSchiller/DNA.doc/blob/master/doc/FROM_ARGS.md).

Since all 11 arguments have to be specified, you should use `-` to denote empty arguments.

## Examples

In the following, we give some simple examples of how to generate datasets using the program:

### (1) undirected random graph with random edge exchanges

The type of graph is specified as `Undirected` (not arguments required, hence `-`).
As initial graph, a random graph is generated (`Random`) with 100 vertices and 500 edges (`100,500`).
For each batch of changes, 10 (`10,999999`) random edge exchanges (`RandomEdgeExchange`) should be generated (with a maximum of 999999 attempts to find an edge pair).
The pseudo random number generator is initialized with the seed 0 (`0`) and a total of 10 batches generated (`10`).
The output is written to example1/ (`example1/`).
The graph is written to example1/0.dnag (`0.dnag`) and the batches to example1/${t}.dnab (`.dnab`) where `${t}` is the target timestamp of the batch.

	java -jar dataset.jar Undirected - Random 100,500 RandomEdgeExchange 10,999999 0 10 example1/ 0.dnag .dnab

Hence, this will result in the following files:

	example1/0.dnag
	example1/1.dnab
	example1/2.dnab
	...
	example1/10.dnab

### (2) directed random graph with growth using preferential attachement

Starts with a directed random graph with 20 vertices and 300 edges.
Then, 100 batches are generated, each adding 10 vertices which create 3 edges each using preferential atachement.

	java -jar dataset.jar Directed - 	Random 20,300 BarabasiAlbert 10,3 0 100 example2/ 0.dnag .dnab


## Download

A build jar file for standalone usage can be downloaded from [dynamic-networks.org](http://dynamic-networks.org/data/jars/DNA.datasets/).

## Building from source

To build the jar file from source (e.g., using the ant build file `build/build.xml`), you need a complete version of the [DNA](https://github.com/BenjaminSchiller/DNA) sources as well as [ArgList](https://github.com/BenjaminSchiller/ArgList).