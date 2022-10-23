### Assumptions
* Data recorded in chronological order (i.e sorted by timestamp).
* A **valid** period is a period which has all data (e.g *n* continuous half hour records) in the input file (i.e no missing entries). 
* The total number of cars fit in Integer.

### How it implements
* The program reads data from a file, it
  * writes the aggregated data to a given file.
  * prints the summary to the standard output.

### How to run
```shell
# args: input-file.txt output-file.txt
java -cp target/aips-1.0-SNAPSHOT.jar com.aips.BatchProcessor whole_year.txt whole_year.out.txt
```

