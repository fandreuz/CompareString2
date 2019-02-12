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
package it.andreuzzi.comparestring2;

import java.lang.reflect.Array;
import it.andreuzzi.comparestring2.AlgMap.Alg;

/**
 * This class contains some useful methods for the {@link Compare} class
 *
 * @author francescoandreuzzi
 */
public class Utils {
    
    /*public static void shift(Object[] array, double[] ranks, Object o, double rank, boolean biggerIsBetter) {
        for(int c = 0; c < array.length; c++) {
            
            boolean case1 = biggerIsBetter && rank > ranks[c];
            boolean case2 = !biggerIsBetter && rank < ranks[c];
            
            if(array[c] == null || case1 || case2) {
                Object temp = array[c];
                double temp2 = ranks[c];
                
                System.arraycopy(array, c, array, c + 1, array.length - (c + 1));
                System.arraycopy(ranks, c, ranks, c + 1, ranks.length - (c + 1));
                
                array[c] = o;
                ranks[c] = rank;
                
                break;
            }
        }
    }*/
    
    /**
     * 
     * @param c         the class that will be used to cast the returned elements
     * @param items     a sorted array of {@link Compare.CompareItem}
     * @param n         the length of the returned array
     * @return          an array of {@code T} containing the first {@code n} elements of {@code items}
     */
    public static <T> T[] gather(Class<T> c, Compare.CompareItem[] items, int n) {
        T[] array = (T[]) Array.newInstance(c, Math.min(n, items.length - 1));
        for(int i = 0; i <= array.length; i++) {
            array[i] = (T) items[i].o;
        }
        
        return array;
    }
    
    /*public static <T> Object[] toArray(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        
        Iterator<T> it = iterable.iterator();
        while(it.hasNext()) {
            list.add(it.next());
        }
        
        return list.toArray(new Object[list.size()]);
    }*/
    
    /**
     * 
     * @param items             a sorted array of {@link Compare.CompareItem}
     * @param deadline          the deadline
     * @param biggerIsBetter    refer to {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) biggerIsBetter(Alg)}
     * @return                  the index of the first element before the first element whose {@code r} value is {@code > deadline} if 
     *                              {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) biggerIsBetter(Alg)} is false, the index of the first element before the first element 
     *                              whose {@code r} value is {@code < deadline} if {@link Utils#biggerIsBetter(ohi.andre.comparestring2.AlgMap.Alg) biggerIsBetter(Alg)} is true
     */
    public static int firstBeyondDeadline(Compare.CompareItem[] items, double deadline, boolean biggerIsBetter) {
        boolean check = false;
        int c = 0;
        for(; c < items.length; c++) {
            boolean case1 = biggerIsBetter && items[c].r < deadline;
            boolean case2 = !biggerIsBetter && items[c].r > deadline;
            
            check = case1 || case2;
            if(check) break;
        }
        
        return c - (check ? 1 : 0);
    }
    
    public static void log(Object o) {
        System.out.println(o != null ? o.toString() : "null");
    }
    
    /**
     * 
     * @param alg
     * @return      return {@code true} if {@code alg} belongs to {@link AlgMap.NormSimAlg}. {@code false} otherwise
     */
    public static boolean biggerIsBetter(Alg alg) {
        return alg instanceof AlgMap.NormSimAlg;
    }
    
    public static float baseRank(Alg alg) {
        return biggerIsBetter(alg) ? Float.MIN_VALUE : Float.MAX_VALUE;
    }
}
