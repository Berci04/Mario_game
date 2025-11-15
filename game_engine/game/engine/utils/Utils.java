/*
 * Copyright (c) 2008 University of Szeged
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package game.engine.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

/**
 * Class for utility functions.
 */
public final class Utils {
  private static final Gson gson = new Gson();
  private static Image[] images = null;

  /**
   * Returns the JSON string representation of the specified object.
   * @param object to be printed
   * @return JSON string
   */
  public static String jsonSerialize(Object object) {
    return gson.toJson(object);
  }
  
  /**
   * Deserializes the specified JSON string to the specified class and returns 
   * the parsed object.
   * @param <T> type to be deserialized
   * @param clazz class of deserialized type
   * @param json string to be parsed
   * @return deserialized object
   */
  public static <T> T jsonDeSerialize(Class<T> clazz, String json) {
    return gson.fromJson(json, clazz);
  }
  
  /**
   * Returns the gson JSON serializer and parser object used in the project.
   * @return gson serializer
   */
  public static Gson getGson() {
    return gson;
  }

  /**
   * Reads and returns an array of images (png type), are found at the 
   * specified path. Their name has to be sequential numbers from 0.
   * @param pathPrefix where search for png-s
   * @return array of found images
   */
  public static Image[] getImages(String pathPrefix) {
    if (images == null) {
      List<Image> imgs = new LinkedList<Image>();
      InputStream is;
      int idx = 0;
      do {
        is = Utils.class.getResourceAsStream(pathPrefix + idx + ".png");
        if (is != null) {
          imgs.add(Toolkit.getDefaultToolkit().getImage(Utils.class.getResource(pathPrefix + idx + ".png")));
        }
        idx ++;
      } while (is != null);
      images = imgs.toArray(new Image[0]);
    }
    return images;
  }
  
  /**
   * Reads and returns an array of images (png type), are found at the 
   * specified path and have the specified names (without file extensions).
   * @param pathPrefix where search for png-s
   * @param fNames name of png-s
   * @return array of images
   */
  public static Image[] getImages(String pathPrefix, String[] fNames) {
    if (images == null) {
      List<Image> imgs = new LinkedList<Image>();
      for (int i = 0; i < fNames.length; i++) {
        imgs.add(Toolkit.getDefaultToolkit().getImage(Utils.class.getResource(pathPrefix + fNames[i] + ".png")));
      }
      images = imgs.toArray(new Image[0]);
    }
    return images;
  }
  
  /**
   * Makes a deep copy of the specified 2D integer array, uses {@link System#arraycopy(Object, int, Object, int, int)}.
   * @param array to be cloned
   * @return a deep copy
   */
  public static int[][] copy(int[][] array) {
    int[][] result = new int[array.length][];
    for (int i = 0; i < array.length; i++) {
      result[i] = new int[array[i].length];
      System.arraycopy(array[i], 0, result[i], 0, array[i].length);
    }
    return result;
  }
  
  /**
   * Copies the values of the first array to the second array.
   * @param src to be cloned
   * @param dst stored to
   */
  public static void set(int[][] src, int[][] dst) {
    for (int i = 0; i < src.length; i++) {
      for (int j = 0; j < src[i].length; j++) {
        dst[i][j] = src[i][j];
      }
    }
  }
  
  /**
   * Shuffles in place the specified array between the specified positions.
   * @param array to be shuffled
   * @param from shuffle from
   * @param to shuffle to
   * @param random random number generator
   */
  public static void shuffle(int[] array, int from, int to, Random random) {
    for (int i = from; i < to-1; i++) {
      int idx = random.nextInt(to - i - 1) + i + 1;
      int tmp = array[i];
      array[i] = array[idx];
      array[idx] = tmp;
    }
  }
  
  /**
   * Copies the and after shuffles the specified array between the specified positions.
   * @param array to be copied and shuffled
   * @param from shuffle from
   * @param to shuffle to
   * @param random random number generator
   * @return copied and shuffled array
   */
  public static int[] copyShuffle(int[] array, int from, int to, Random random) {
    int[] copy = Arrays.copyOf(array, array.length);
    shuffle(copy, from, to, random);
    return copy;
  }
}
