import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class DynamicMemoryAllocation {

  private int numHoles;
  private int numProcesses;

  private int[] holeSize;
  private int[] processSize;

  public DynamicMemoryAllocation(int h, int p) {

    this.numHoles = h;
    this.numProcesses = p;

    this.holeSize = new int[h];
    this.processSize = new int[p];
  }


  // Read hole sizes and process sizes from input file
  public boolean readFromFile(String filename) {

    try {

      Scanner fileScanner = new Scanner(new File(filename));

      // Read Hole Sizes
      for (int i = 0; i < numHoles; i++) {

        if (fileScanner.hasNextInt())

          holeSize[i] = fileScanner.nextInt();

        else {

          fileScanner.close();
          return false;
        }
      }


      // Read Process Sizes
      for (int i = 0; i < numProcesses; i++) {

        if (fileScanner.hasNextInt())

          processSize[i] = fileScanner.nextInt();

        else {

          fileScanner.close();
          return false;
        }
      }


      fileScanner.close();

      return true;

    } catch (FileNotFoundException e) {

      return false;
    }
  }


  // Generate random values
  public void generateRandomData() {

    Random rand = new Random();

    for (int i = 0; i < numHoles; i++)

      holeSize[i] = (rand.nextInt(10) + 1) * 100;


    for (int i = 0; i < numProcesses; i++)

      processSize[i] = (rand.nextInt(9) + 1) * 50;
  }


  public void displayInitialData() {

    System.out.print("\nMemory Holes: ");

    for (int i = 0; i < numHoles; i++) {

      System.out.print(
          "H" + (i + 1) + " = " + holeSize[i] + "KB");

      if (i < numHoles - 1)

        System.out.print(", ");
    }


    System.out.print("\nProcesses: ");

    for (int i = 0; i < numProcesses; i++) {

      System.out.print(
          "P" + (i + 1) + " = " + processSize[i] + "KB");

      if (i < numProcesses - 1)

        System.out.print(", ");
    }

    System.out.println();
  }


  private void printHeading(String name) {

    System.out.println(
        "\n" + name +
        " DYNAMIC MEMORY_HOLE ALLOCATION PROCEDURE");

    System.out.println(
        "=================================================");


    System.out.printf(
        "%-10s %-15s %-15s %-12s %-18s%n",
        "Process",
        "Process Size",
        "Hole selected",
        "Hole Size",
        "Left_over space");


    System.out.printf(
        "%-10s %-15s %-15s %-12s %-18s%n",
        "=======",
        "============",
        "=============",
        "=========",
        "===============");
  }


  private void printRow(
      int processNo,
      int pSize,
      int holeIndex,
      int selectedHoleSize,
      int leftover) {


    String selected;


    if (holeIndex == -1)

      selected = "-NOT-";

    else

      selected = "H" + (holeIndex + 1);


    System.out.printf(
        "%-10s %-15s %-15s %-12s %-18s%n",

        "P" + processNo,

        pSize + " KB",

        selected,

        selectedHoleSize + " KB",

        leftover + " KB");
  }


  private int totalRemaining(int[] holes) {

    int total = 0;

    for (int i = 0; i < holes.length; i++)

      total += holes[i];

    return total;
  }


  /*
   order = 0 -> original order
   order = 1 -> ascending order
   order = 2 -> descending order
  */
  private void printRemaining(
      String name,
      int[] holes,
      int order) {


    int[] display = new int[holes.length];


    for (int i = 0; i < holes.length; i++)

      display[i] = holes[i];


    // Sort only for displaying
    if (order != 0) {

      for (int i = 0; i < display.length - 1; i++) {

        for (int j = i + 1; j < display.length; j++) {


          if (
              (order == 1 &&
               display[i] > display[j])

              ||

              (order == 2 &&
               display[i] < display[j])
             ) {


            int temp = display[i];

            display[i] = display[j];

            display[j] = temp;
          }
        }
      }
    }


    System.out.println(
        "\nThe following Holes remains after the "
        + name + " procedure:");


    System.out.print("{");


    for (int i = 0; i < display.length; i++) {

      System.out.print(display[i] + "KB");

      if (i < display.length - 1)

        System.out.print(", ");
    }


    System.out.println(
        "} ==> Total "
        + totalRemaining(holes)
        + "KB.");
  }


  // ==================================================
  // FIRST FIT
  // ==================================================

  public int firstFit() {

    int[] holes = new int[numHoles];


    for (int i = 0; i < numHoles; i++)

      holes[i] = holeSize[i];


    printHeading("FIRST-FIT");


    for (int i = 0; i < numProcesses; i++) {

      int selected = -1;


      // Search from first hole
      for (int j = 0; j < numHoles; j++) {

        if (holes[j] >= processSize[i]) {

          selected = j;

          break;
        }
      }


      if (selected != -1) {

        int before = holes[selected];


        holes[selected] =
            holes[selected] - processSize[i];


        printRow(
            i + 1,
            processSize[i],
            selected,
            before,
            holes[selected]);


      } else {


        printRow(
            i + 1,
            processSize[i],
            -1,
            0,
            0);
      }
    }


    printRemaining(
        "FIRST-FIT",
        holes,
        0);


    return totalRemaining(holes);
  }


  // ==================================================
  // BEST FIT
  // ==================================================

  public int bestFit() {

    int[] holes = new int[numHoles];


    for (int i = 0; i < numHoles; i++)

      holes[i] = holeSize[i];


    printHeading("BEST-FIT");


    for (int i = 0; i < numProcesses; i++) {

      int selected = -1;


      // Find smallest possible hole
      for (int j = 0; j < numHoles; j++) {


        if (holes[j] >= processSize[i]) {


          if (selected == -1 ||

              holes[j] < holes[selected]) {


            selected = j;
          }
        }
      }


      if (selected != -1) {

        int before = holes[selected];


        holes[selected] =
            holes[selected] - processSize[i];


        printRow(
            i + 1,
            processSize[i],
            selected,
            before,
            holes[selected]);


      } else {


        printRow(
            i + 1,
            processSize[i],
            -1,
            0,
            0);
      }
    }


    // Teacher's answer displays
    // BEST-FIT remaining holes ascending
    printRemaining(
        "BEST-FIT",
        holes,
        1);


    return totalRemaining(holes);
  }


  // ==================================================
  // WORST FIT
  // ==================================================

  public int worstFit() {

    int[] holes = new int[numHoles];


    for (int i = 0; i < numHoles; i++)

      holes[i] = holeSize[i];


    printHeading("WORST-FIT");


    for (int i = 0; i < numProcesses; i++) {

      int selected = -1;


      // Find largest possible hole
      for (int j = 0; j < numHoles; j++) {


        if (holes[j] >= processSize[i]) {


          if (selected == -1 ||

              holes[j] > holes[selected]) {


            selected = j;
          }
        }
      }


      if (selected != -1) {

        int before = holes[selected];


        holes[selected] =
            holes[selected] - processSize[i];


        printRow(
            i + 1,
            processSize[i],
            selected,
            before,
            holes[selected]);


      } else {


        printRow(
            i + 1,
            processSize[i],
            -1,
            0,
            0);
      }
    }


    // Teacher's answer displays
    // WORST-FIT remaining holes descending
    printRemaining(
        "WORST-FIT",
        holes,
        2);


    return totalRemaining(holes);
  }


  // ==================================================
  // RUN ALL THREE
  // ==================================================

  public void run() {

    int best = bestFit();

    int first = firstFit();

    int worst = worstFit();


    System.out.println("\nConclusion:");

    System.out.println(
        "------------------------------------------------------------");


    // All three procedures have the same total hole space
    if (best == first && first == worst) {

        System.out.println(
            "All of the given Memory_hole allocation procedures are suitable.");


    // BEST-FIT has the smallest total hole space
    } else if (best < first && best < worst) {

        System.out.println(
            "The BEST-FIT Procedure is the suitable one "
            + "for the given Processes.");


    // FIRST-FIT has the smallest total hole space
    } else if (first < best && first < worst) {

        System.out.println(
            "The FIRST-FIT Procedure is the suitable one "
            + "for the given Processes.");


    // WORST-FIT has the smallest total hole space
    } else if (worst < best && worst < first) {

        System.out.println(
            "The WORST-FIT Procedure is the suitable one "
            + "for the given Processes.");


    // BEST-FIT and FIRST-FIT tie
    } else if (best == first && best < worst) {

        System.out.println(
            "The BEST-FIT and FIRST-FIT Procedures are suitable "
            + "for the given Processes.");


    // BEST-FIT and WORST-FIT tie
    } else if (best == worst && best < first) {

        System.out.println(
            "The BEST-FIT and WORST-FIT Procedures are suitable "
            + "for the given Processes.");


    // FIRST-FIT and WORST-FIT tie
    } else if (first == worst && first < best) {

        System.out.println(
            "The FIRST-FIT and WORST-FIT Procedures are suitable "
            + "for the given Processes.");
    }


    System.out.println(
        "------------------------------------------------------------");
}


  // ==================================================
  // MAIN
  // ==================================================

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);

    String continueChoice;


    do {

      System.out.print(
          "\n\n\tDynamic Memory Hole Allocation");

      System.out.print(
          "\n\t==============================\n\n");


      System.out.print(
          "Enter Hole size: ");

      int h = scanner.nextInt();


      System.out.print(
          "Enter Process size: ");

      int p = scanner.nextInt();


      DynamicMemoryAllocation memory =
          new DynamicMemoryAllocation(h, p);


      System.out.print(
          "Enter I to get input data from a file "
          + "or press any key for random data: ");


      String inputType = scanner.next();


      if (inputType.equalsIgnoreCase("I")) {


        System.out.print(
            "Enter the file name "
            + "(with .txt extension): ");


        String filename = scanner.next();


        if (memory.readFromFile(filename)) {


          System.out.println(
              "\nFile opened successfully "
              + "and data transfered into arrays.");


        } else {


          System.out.println(
              "Failed to read valid data. "
              + "Exiting this run.");


          continueChoice = "Y";

          continue;
        }


      } else {


        memory.generateRandomData();


        System.out.println(
            "\nRandom data generated successfully.");
      }


      memory.displayInitialData();


      memory.run();


      System.out.print(
          "\nEnter Y to continue "
          + "or any key to Exit: ");


      continueChoice = scanner.next();


    } while (
        continueChoice.equalsIgnoreCase("Y"));


    scanner.close();
  }
}