/* Don't need to import anything belonging to java.lang.*
 *
 * Compile: javac Examples.java
 * Run: java Examples
 */

public class Examples {

  /**
   * Retrieve the component type of any array that is passed in.
   *
   * @param arr The array whose component type will be returned.
   * @return The component type of the provided array.
   * @throws IllegalArgumentException If the provided argument is not an array.
   */
  public static void getArrayComponentType(Object arr)
    throws IllegalArgumentException {
    Class<?> arrClass = arr.getClass();

    if (arrClass.isArray() != true) {
      throw new IllegalArgumentException();
    }

    System.out.println(arrClass.getComponentType());
  }

  public static void main(String args[]) {
    //System.out.println("Hello, world!");

    /* Test out the getArrayComponentType function. */
    int[] test = new int[10];
    getArrayComponentType(test);
  }
}
