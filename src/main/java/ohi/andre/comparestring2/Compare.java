/*
* The MIT License
*
* Copyright 2019 francescoandreuzzi.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/


package ohi.andre.comparestring2;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;
import ohi.andre.comparestring2.AlgMap.Alg;
import ohi.andre.comparestring2.algs.interfaces.Algorithm;

/**
 * 
 * The core class of CompareString2. The class {@code Compare} contains the methods used to compare {@link String} objects. 1vs1 or 1vsMany
 *
 * @author francescoandreuzzi
 */
public class Compare {
    
    /**
     * 
     * @param s1          the {@link String} string of the comparison
     * @param s2          the {@link String} string of the comparison
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            the distance/similarity between {@code s1} and {@code s2}
     * @see               Algorithm
    */
    public static float compare(String s1, String s2, Algorithm algInstance, Alg alg) {
        return alg.compare(algInstance, s1, s2);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param s2          the second {@link String} of the comparison
     * @param alg         an instance of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that will be used to build an instance of {@link Algorithm}
     * @return            the distance/similarity between {@code s1} and {@code s2}
     * @see               Alg
    */
    public static float compare(String s1, String s2, Alg alg, Object... args) {
        return alg.compare(s1, s2, args);
    }
    
//    ----------------------------------------------------------------------------------------------------------------
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param s2          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param size        the exact number of elements in {@code s2}
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@link CompareItem} containing information about the comparison of each element with {@code s1}. 
     *                      The array is sorted with respect of the sorting order given by the chosen {@link Algorithm}
     * @see               CompareItem
     */
    private static <T> CompareItem[] buildComparePack(String s1, Iterable<T> s2, int size, String[] splitters, Algorithm algInstance, Alg alg) {
        final String ss1 = normalize(s1);
        
        CompareItem[] toReturn = new CompareItem[size];
        Iterator<? extends Object> it = s2.iterator();
        
        int counter = 0;
        while(it.hasNext()) {
            float result;
            
            Object t = it.next();
            String st = normalize(t.toString());
            
            if(splitters != null) {
                result = checkSplits(ss1, st, splitters, algInstance, alg);
            } else {
                result = compare(ss1, st, algInstance, alg);
            }
            
            toReturn[counter++] = new CompareItem(t, result);
        }
        
        Arrays.sort(toReturn, Utils.biggerIsBetter(alg) ? bibCmp : bnbCmp);
        return toReturn;
    }
    
    /**
     * the comparator used when {@code biggerIsBetter} is true
     */
    private static final Comparator<CompareItem> bibCmp = (CompareItem o1, CompareItem o2) -> {
        return (int) Math.signum(o2.r - o1.r);
    };
    
    /**
     * the comparator used when {@code biggerIsBetter} is false
     */
    private static final Comparator<CompareItem> bnbCmp = (CompareItem o1, CompareItem o2) -> {
        return (int) Math.signum(o1.r - o2.r);
    };
    
//    ----------------------------------------------------------------------------------------------------------------
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            the single best match of {@code s1} with each element of {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, T[] ss, Alg alg, Object... args) {
        return bestMatch(s1, ss, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            the single best match of {@code s1} with each element of {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, T[] ss, Algorithm algInstance, Alg alg) {
        return bestMatch(s1, ss, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            the single best match of {@code s1} with each element of {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, T[] ss, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return bestMatch(s1, ss, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            the single best match of {@code s1} with each element of {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, T[] ss, String[] splitters, Algorithm algInstance, Alg alg) {
        T[] matches = topNmatches(s1, ss, 1, splitters, algInstance, alg);
        if(matches == null || matches.length == 0) return null;
        return matches[0];
    }
    
//    --------------------------------------------------------
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the results given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, T[] ss, int n, Alg alg, Object... args) {
        return topNmatches(s1, ss, n, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the results given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, T[] ss, int n, Algorithm algInstance, Alg alg) {
        return topNmatches(s1, ss.length, Arrays.asList(ss), n, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the ranks given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, T[] ss, int n, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return topNmatches(s1, ss, n, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the results given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, T[] ss, int n, String[] splitters, Algorithm algInstance, Alg alg) {
        return topNmatches(s1, ss.length, Arrays.asList(ss), n, splitters, algInstance, alg);
    }
    
//    --------------------------------------------------------
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, T[] ss, float deadline, Alg alg, Object... args) {
        return withDeadline(s1, ss, deadline, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, T[] ss, float deadline, Algorithm algInstance, Alg alg) {
        return withDeadline(s1, ss.length, Arrays.asList(ss), deadline, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, T[] ss, float deadline, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return withDeadline(s1, ss, deadline, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, T[] ss, float deadline, String[] splitters, Algorithm algInstance, Alg alg) {
        return withDeadline(s1, ss.length, Arrays.asList(ss), deadline, splitters, algInstance, alg);
    }
    
//    --------------------------------------------------------

    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, T[] ss, int n, float deadline, Alg alg, Object... args) {
        return topMatchesWithDeadline(s1, ss, n, deadline, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, T[] ss, int n, float deadline, Algorithm algInstance, Alg alg) {
        return topMatchesWithDeadline(s1, ss.length, Arrays.asList(ss), n, deadline, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, T[] ss, int n, float deadline, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return topMatchesWithDeadline(s1, ss, n, deadline, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param ss          the {@code T[]} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, T[] ss, int n, float deadline, String[] splitters, Algorithm algInstance, Alg alg) {
        return topMatchesWithDeadline(s1, ss.length, Arrays.asList(ss), n, deadline, splitters, algInstance, alg);
    }
    
//    ----------------------------------------------------------------------------------------------------------------
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            the best match with {@code s1} within {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, int size, Iterable<T> ss, Alg alg, Object... args) {
        return bestMatch(s1, size, ss, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            the best match with {@code s1} within {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, int size, Iterable<T> ss, Algorithm algInstance, Alg alg) {
        return bestMatch(s1, size, ss, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            the best match with {@code s1} within {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, int size, Iterable<T> ss, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return bestMatch(s1, size, ss, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            the best match with {@code s1} within {@code ss}, found using the given {@link Alg} {@code alg}
     */
    public static <T> T bestMatch(String s1, int size, Iterable<T> ss, String[] splitters, Algorithm algInstance, Alg alg) {
        T[] matches = topNmatches(s1, size, ss, 1, splitters, algInstance, alg);
        if(matches == null || matches.length == 0) return null;
        return matches[0];
    }
    
//    --------------------------------------------------------
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the ranks given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, int size, Iterable<T> ss, int n, Alg alg, Object... args) {
        return topNmatches(s1, size, ss, n, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the ranks given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, int size, Iterable<T> ss, int n, Algorithm algInstance, Alg alg) {
        return topNmatches(s1, size, ss, n, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the ranks given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, int size, Iterable<T> ss, int n, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return topNmatches(s1, size, ss, n, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the length of the returned array object
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} of length {@code n}. The sorting operation is based on the ranks given by the algorithm {@code alg}
     */
    public static <T> T[] topNmatches(String s1, int size, Iterable<T> ss, int n, String[] splitters, Algorithm algInstance, Alg alg) {
        Class<T> clazz = (Class<T>) ss.iterator().next().getClass();
        
        CompareItem[] items = buildComparePack(s1, ss, size, splitters, algInstance, alg);
        return Utils.gather(clazz, items, n);
    }
    
//    --------------------------------------------------------
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, int size, Iterable<T> ss, float deadline, Alg alg, Object... args) {
        return withDeadline(s1, size, ss, deadline, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, int size, Iterable<T> ss, float deadline, Algorithm algInstance, Alg alg) {
        return withDeadline(s1, size, ss, deadline, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, int size, Iterable<T> ss, float deadline, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return withDeadline(s1, size, ss, deadline, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing only elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] withDeadline(String s1, int size, Iterable<T> ss, float deadline, String[] splitters, Algorithm algInstance, Alg alg) {
        Class<T> clazz = (Class<T>) ss.iterator().next().getClass();
        
        CompareItem[] items = buildComparePack(s1, ss, size, splitters, algInstance, alg);
        int cutIndex = Utils.firstBeyondDeadline(items, deadline, Utils.biggerIsBetter(alg));
        return Utils.gather(clazz, items, cutIndex);
    }
    
//    --------------------------------------------------------    
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, int size, Iterable<T> ss, int n, float deadline, Alg alg, Object... args) {
        return topMatchesWithDeadline(s1, size, ss, n, deadline, null, alg, args);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, int size, Iterable<T> ss, int n, float deadline, Algorithm algInstance, Alg alg) {
        return topMatchesWithDeadline(s1, size, ss, n, deadline, null, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @param args        a list of Object that can be used to build an instance of {@link Algorithm}
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, int size, Iterable<T> ss, int n, float deadline, String[] splitters, Alg alg, Object... args) {
        Algorithm algInstance = alg.buildAlg(args);
        return topMatchesWithDeadline(s1, size, ss, n, deadline, splitters, algInstance, alg);
    }
    
    /**
     * 
     * @param s1          the first {@link String} of the comparison
     * @param size        the number of elements in {@code ss}
     * @param ss          an {@link Iterable<T>} object whose elements will be compared to {@code s1}
     * @param n           the maximum number of elements of the returned array
     * @param deadline    the min/max rank of the elements in the array which is returned
     * @param splitters   refer to {@link Compare#checkSplits checkSplits}
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance of {@link Alg}. It must refer to the same algorithm referred by {@code algInstance}. Check {@link AlgMap} 
     * @return            a sorted array of {@code T} containing at most {@code n} elements with {@code rank >= deadline} if 
     *                      {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code true}, only elements with
     *                      {@code rank <= deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) Utils.biggerIsBetter(alg)} is {@code false}
     */
    public static <T> T[] topMatchesWithDeadline(String s1, int size, Iterable<T> ss, int n, float deadline, String[] splitters, Algorithm algInstance, Alg alg) {
        Class<T> clazz = (Class<T>) ss.iterator().next().getClass();
        
        CompareItem[] items = buildComparePack(s1, ss, size, splitters, algInstance, alg);
        int cutIndex = Utils.firstBeyondDeadline(items, deadline, Utils.biggerIsBetter(alg));
        return Utils.gather(clazz, items, Math.min(cutIndex, n));
    }
    
//    ----------------------------------------------------------------------------------------------------------------    
    
    /**
     * This method affects the overall performance of the comparison. You should choose carefully your {@code splitters}.
     * 
     * @param s1          the first {@link String} of the comparison
     * @param s2          the second {@link String} of the comparison
     * @param splitters   each element of {@code splitters} will be used to perform a call to {@code s2.split(element)}. Each element of the returned array
     *                      will be compared to {@code s1} using the provided algorithm. Then the value of the single best match (the biggest or the smallest, depending on {@code alg}) 
     *                      will be returned.
     * @param algInstance an instance of {@link Algorithm} that will be used to perform the comparison
     * @param alg         an instance  of {@link Alg}. Check {@link AlgMap} 
     * @return            the value of the best match
     */
    private static float checkSplits(String s1, String s2, String[] splitters, Algorithm algInstance, AlgMap.Alg alg) {
        boolean biggerIsBetter = Utils.biggerIsBetter(alg);
        float result = Utils.baseRank(alg);
        
        for(String q : splitters) {
            String[] split = s2.split(Pattern.quote(q));
            for(int i = 1; i < split.length; i++) {
                float r = compare(s1, split[i], algInstance, alg);
                result = biggerIsBetter ? Math.max(result, r) : Math.min(result, r);
            }
        }
        
//        test the whole word
        float r = compare(s1, s2, algInstance, alg);
        result = biggerIsBetter ? Math.max(result, r) : Math.min(result, r);
        
        return result;
    }
    
    private static Pattern accentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final String EMPTYSTRING = "";
    
    /**
     * 
     * @param s the {@link String} you want to normalize
     * @return  {@code s}, lowercase, without accents, or similar weird characters
     */
    public static String normalize(String s) {
        String decomposed = Normalizer.normalize(s, Normalizer.Form.NFD);
        return accentPattern.matcher(decomposed).replaceAll(EMPTYSTRING).toLowerCase();
    }
    
    /**
     * This class holds the result of a comparison
     * {@code o} is the object compared to {@code s1}
     * {@code r} is the result of the comparison
     */
    protected static class CompareItem {
        Object o;
        float r;
        
        public CompareItem(Object obj, float rank) {
            this.o = obj;
            this.r = rank;
        }

        @Override
        public String toString() {
            return o.toString() + " - " + r;
        }
    }
}
