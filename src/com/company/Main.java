package com.company;

import com.company.collections.changeAPI.Change;
import com.company.collections.changeAPI.changes.singlethread.SingleThreadChange;
import com.company.collections.changeAPI.generation.RandomIntGenerator;
import com.company.utilities.MathUtil;
import com.company.utilities.StringUtil;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        // =============================================================================================================
        //                                                 S E T U P
        // =============================================================================================================
        //
        // /!\ this project require Java 17 preview features !
        //
        //      -> just run it once and when it crashes it will bring you to a switch statement. Alt + return it and
        //         choose "Set language level to 17 (Preview) - Pattern matching for switch".
        //
        // /!\ this project require jetbrains annotation !
        //
        //      -> go to ArrayUtil and look for @NotNull annotation. Alt + return it and choose "add 'annotations' to
        //         classpath" then click "ok" if a dialog box pops up.
        //
        // =============================================================================================================
        //                                                   I N F O
        // =============================================================================================================
        //
        // Here's a quick example of the Change API I've been working on. This really just scratches the surface of all
        // the available methods there are for manipulating arrays. (You can have a look at Change.java in
        // collections/changeAPI if you want more info on what each method does or how the API works as a whole)
        //
        // Just give it a try in what you think are typical array / Collections use-cases and tell me if it's intuitive
        // and or useful enough ;)
        //
        // PS: you might also want to try it on really massive arrays at some point as well. This was initially meant
        // for image processing so arrays of over 2_000_000 values with lots of removal / replacements, and its really
        // in those circumstances that it blows typical Java Collection out of the water. I've already set up a quick
        // test down bellow you can use if you want

        final SingleThreadChange<Integer> change =
            Change.of(Integer.class)                           // initialises the change as an empty array of Integers
                  .addAll(1, 9, 7, 0, 9, 9, 3, 1, 0) // populates the array
                  .replaceAt(
                        0, 12,                        // replaces element at index 0 by 12
                        5, 42,                                 // replaces element at index 5 by 42
                        7, -12                                 // replaces element at index 7 by -12
                  )
                  .forEach(integer -> integer * 2)             // multiplies every element in the array by 2
                  .retainIf(integer -> integer > 0)            // retains only elements which are superior to 0
                  .unique()                                    // retains only unique elements in the array
                  .sorted()                                    // sorts the array
                  .optimise();                                 // youuu should read the javaDoc for this one,
                                                               // it's a bit complicated...

        System.out.println(">> FINAL ARRAY");
        System.out.println(Arrays.toString(change.toArray())); // retrieves the array
        System.out.println(">> SUM OF ALL ELEMENTS");
        System.out.println(change.sumOf());                    // sums up every element in the array

        // =============================================================================================================
        //      A quick stress-test (can take over a minute because ArrayList is kind of the bottleneck here haha)
        // =============================================================================================================

        // set to false by default to avoid unwanted performance hits
        final boolean RUN_STRESS_TEST = true;

        if (!RUN_STRESS_TEST) return;

        final int RANDOM_RANGE = (int) Math.pow(255, 3);   // amount of possible pixel variety
        final int TEST_SIZE = 2_073_600;                // the amount of pixels in a 1920 x 1080 image is 2_073_600
        final int CHANGE_SIZE = 10_000;                   // size of the change to be applied

        // initialises the array of pixel, making use of the Generator interface
        // (this simplifies the generation of arrays by encapsulating the usual for-loops inside of generator classes
        // which define how to generate new values on a per-element basis, making the main code more readable by
        // separating generation from utilisation)
        final Integer[] pixels = new RandomIntGenerator(RANDOM_RANGE).generateArray(Integer.class, TEST_SIZE);

        // initialises the array of values to remove
        // (had to keep it pretty low because of ArrayList's poor performance, but you can totally try it out for
        // 500_000 and upwards if you're just using the Change API)
        final Integer[] values = new RandomIntGenerator(RANDOM_RANGE).generateArray(Integer.class, CHANGE_SIZE);

        // times how long it takes for the Change API to remove all occurrences of the elements to remove
        // (keep in mind that because of the way in which the Change API works the removal only applies when calling
        // toArray, so as a developer this gives you more control as to when to take the performance hit and be able
        // to better handle it)
        final long changeStart = System.currentTimeMillis();
        final Integer[] testChange = Change.of(pixels).removeAll(values).toArray();
        final long changeStop = System.currentTimeMillis();

        // times how long it takes for the Change API to remove all occurrences of the elements to remove IN PARALLEL
        // (The Change API was originally built with image manipulation in mind. One of the main issues with image
        // manipulation is handling large arrays efficiently, which often results in complicated multithreaded
        // algorithms. The Change API has this in-built, making it easy to take advantage of the performance boost
        // offered by parallelization without having to make the effort necessary to implement such algorithms)
        final long parallelChangeStart = System.currentTimeMillis();
        final Integer[] parallelTestChange = Change.of(pixels).parallel().removeAll(values).toArray();
        final long parallelChangeStop = System.currentTimeMillis();

        // times how long it takes for the ArrayList to remove all occurrences of the elements to remove
        // (also this is much less readable)
        final long listStart = System.currentTimeMillis();
        final List<Integer> arrayList = new ArrayList<>(TEST_SIZE);
        arrayList.addAll(Arrays.asList(pixels));
        arrayList.removeAll(Arrays.asList(values));
        final long listStop = System.currentTimeMillis();

        // checks for any discrepancies in the size of the array after all values have been removed
        boolean removalDiscrepancies =
                arrayList.size() != testChange.length ||
                arrayList.size() != parallelTestChange.length;

        // checks for any discrepancies in values after all values have been removed
        // (only if both arrayList and testChange have the same size, otherwise might result in an
        // IndexOutOfBoundsException)
        if (!removalDiscrepancies) {
            for (int i = 0; i < testChange.length; i++) {
                // makes sure the elements in each array are the same
                final boolean error =
                        !Objects.equals(arrayList.get(i), testChange[i]) ||
                        !Objects.equals(arrayList.get(i), parallelTestChange[i]);

                // if a discrepancy is encountered, exits the check
                if (error) {
                    removalDiscrepancies = true;
                    break;
                }
            }
        }

        // calculates the time taken by both the Change API and ArrayList
        final long changeDuration = changeStop - changeStart;
        final long parallelChangeDuration = parallelChangeStop - parallelChangeStart;
        final long listDuration = listStop - listStart;

        // displays the results
        System.out.println(">> ERRORS IN REMOVAL");
        System.out.println(removalDiscrepancies ? "YES" : "NONE");
        System.out.println(">> ARRAY SIZE");
        System.out.println("Change API (single thread): " + StringUtil.makeReadableNumber(testChange.length));
        System.out.println("Change API (multithreaded): " + StringUtil.makeReadableNumber(parallelTestChange.length));
        System.out.println("ArrayList : "                 + StringUtil.makeReadableNumber(arrayList.size()));
        System.out.println(">> ALGORITHMS DURATION");
        System.out.println("Change API (single thread): " + StringUtil.makeReadableNumber(changeDuration) + "ms");
        System.out.println("Change API (multithreaded): " + StringUtil.makeReadableNumber(parallelChangeDuration) + "ms");
        System.out.println("ArrayList                 : " + StringUtil.makeReadableNumber(listDuration) + "ms");

        // compares the best-time performance of the Change API to the time performance of ArrayList
        System.out.println(performanceMessage(Math.min(changeDuration, parallelChangeDuration), listDuration));

    }

    /**
     * Generates a message comparing the performance of the {@link Change} API and {@link ArrayList}
     * @param changeDuration ({@code long}): the time taken by the Change API
     * @param listDuration ({@code long}): the time taken by ArrayList
     * @return (String): performance message comparing the Change API and ArrayList
     */
    private static String performanceMessage(final long changeDuration, final long listDuration) {
        final double improvement;

        // Change API is faster
        if (changeDuration < listDuration) {
            improvement = MathUtil.roundToPrecision((double) listDuration / changeDuration, 2);
            return "The Change API is " + improvement + "x faster than ArrayList";
        }
        // ArrayList is faster
        else if (listDuration < changeDuration) {
            improvement = MathUtil.roundToPrecision((double) changeDuration / listDuration, 2);
            return "ArrayList is " + improvement + "x faster than the Change API";
        }

        // Both collections are tied
        return "The Change API and ArrayList are tied";
    }
}
