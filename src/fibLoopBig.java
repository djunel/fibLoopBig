import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;
import java.io.*;
import java.util.*;
import java.math.*;

public class fibLoopBig {

        static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

        /* define constants */
        static long MAXVALUE =  2000000000;
        static long MINVALUE = -2000000000;
        static int numberOfTrials = 100;
        static int MAXINPUTSIZE  = (int) Math.pow(1.5,28);
        static int MININPUTSIZE  =  1;
        static int Nums = 999;
        static BigInteger fibResult = BigInteger.valueOf(0);

        // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

        static String ResultsFolderPath = "/home/diana/Results/"; // pathname to results folder
        static FileWriter resultsFile;
        static PrintWriter resultsWriter;

        public static void main(String[] args) {

            // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized

            System.out.println("Running first full experiment...");
            runFullExperiment("FibLoopBig-Exp1-ThrowAway.txt");
            System.out.println("Running second full experiment...");
            runFullExperiment("FibLoopBig-Exp2.txt");
            System.out.println("Running third full experiment...");
            runFullExperiment("FibLoopBig-Exp3.txt");
        }

        static void runFullExperiment(String resultsFileName){
            //declare variables for doubling ratio
            double[] averageArray = new double[1000];
            double currentAv = 0;
            double doublingTotal = 0;
            int x = 0;


            //set up print to file
            try {
                resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
                resultsWriter = new PrintWriter(resultsFile);
            } catch(Exception e) {
                System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
                return; // not very foolproof... but we do expect to be able to create/open the file...
            }

            //declare variables for stop watch
            ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
            ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

            //add headers to text file
            resultsWriter.println("#X(Value)  N(Size)  AverageTime        FibNumber   NumberOfTrials"); // # marks a comment in gnuplot data
            resultsWriter.flush();

            /* for each size of input we want to test: in this case starting small and doubling the size each time */
            for(int inputSize=0;inputSize<=Nums; inputSize++) {
                //test run for fibonacci numbers
                verifyFib(inputSize);

                // progress message...
                System.out.println("Running test for input size "+inputSize+" ... ");

                /* repeat for desired number of trials (for a specific size of input)... */
                long batchElapsedTime = 0;
                // generate a list of randomly spaced integers in ascending sorted order to use as test input
                // In this case we're generating one list to use for the entire set of trials (of a given input size)
                // but we will randomly generate the search key for each trial
                //System.out.print("    Generating test data...");

                //generate random integer list
                //long resultFib = Fib(x);

                //print progress to screen
                //System.out.println("...done.");
                System.out.print("    Running trial batch...");

                /* force garbage collection before each batch of trials run so it is not included in the time */
                System.gc();


                // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
                // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
                // stopwatch methods themselves
                BatchStopwatch.start(); // comment this line if timing trials individually

                // run the trials
                for (long trial = 0; trial < numberOfTrials; trial++) {
                    // generate a random key to search in the range of a the min/max numbers in the list
                    //long testSearchKey = (long) (0 + Math.random() * (testList[testList.length-1]));
                    /* force garbage collection before each trial run so it is not included in the time */
                    // System.gc();

                    //TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                    /* run the function we're testing on the trial input */

                    fibResult = Fib(inputSize);
                    //System.out.println(result);
                    // batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
                }
                batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
                double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

                //put current average time in array of average times. We will be able to use this to calculate the doubling ratio
                averageArray[x] = averageTimePerTrialInBatch;

                //skip this round if this is the first one (no previous average for calculation)
                if(inputSize != 0){
                    doublingTotal = averageTimePerTrialInBatch/averageArray[x-1]; //Calculate doubling ratio

                }
                x++;
                int countingbits = countBits(inputSize);
                /* print data for this size of input */
                resultsWriter.printf("%6d %6d %15.2f %d %4d\n",inputSize, countingbits, averageTimePerTrialInBatch, fibResult, numberOfTrials); // might as well make the columns look nice
                resultsWriter.flush();
                System.out.println(" ....done.");
            }
        }

        /*Verify merge sort is working*/
        static void verifyFib(int x){

            System.out.println("Testing..." + x + " = " + Fib(x));
        }

        public static BigInteger Fib(int x){
            /*Declare Variables*/
            BigInteger result = BigInteger.valueOf(0);
            BigInteger temp1 = BigInteger.valueOf(0);;
            BigInteger temp2 = BigInteger.valueOf(0);;



            //loop through to x
            for(int i = 0; i <=x; i++){
                //if number is 0 or 1, result will be 1
                if(i == 0 || i == 1){
                    result = BigInteger.valueOf(1);
                    //save the last fib number in temp 1 variable
                    temp1 = temp2;
                    //save the result in temp 2 variable
                    temp2 = result;
                }
                else{
                    //if number is higher than 1, add the last two numbers together
                    result =   temp1.add(temp2);
                    //save the last fib number as temp 1
                    temp1 = temp2;
                    //save the result as temp 1
                    temp2 = result;
                }

            }
            //return result
            return result;
        }

        //count the number of bits required for current fib number
        static int countBits(int n)
        {
            int count = 0;
            //if n == 0, count will be 1
            if(n == 0){
                count = 1;
            }
            //loop while n does not equal 0
            while (n != 0)
            {
                //each loop add 1 to count
                count++;
                //shift n to the left by 1
                n >>= 1;
            }
            //System.out.println("number of bits = " + count);
            return count;
        }


}
