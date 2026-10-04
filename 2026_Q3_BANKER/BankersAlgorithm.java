import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BankersAlgorithm {
  private int numResources;
  private int numProcesses;
  private int[][] maxMatrix;
  private int[][] allocationMatrix;
  private int[][] needMatrix;
  private int[] totalResources;
  private int[] availableVector;
  private boolean[] finish;
  private ArrayList<Integer> safeSequence;

  public BankersAlgorithm(int r, int p) {
    this.numResources = r;
    this.numProcesses = p;
    this.maxMatrix = new int[p][r];
    this.allocationMatrix = new int[p][r];
    this.needMatrix = new int[p][r];
    this.totalResources = new int[r];
    this.availableVector = new int[r];
    this.finish = new boolean[p];
    this.safeSequence = new ArrayList<>();
  }

  private void calculateNeed() {
    for (int i = 0; i < numProcesses; i++) {
      for (int j = 0; j < numResources; j++) {
        needMatrix[i][j] = maxMatrix[i][j] - allocationMatrix[i][j];
      }
    }
  }

  private void calculateInitialAvailable() {
    for (int j = 0; j < numResources; j++) {
      int sumAllocated = 0;
      for (int i = 0; i < numProcesses; i++) sumAllocated += allocationMatrix[i][j];
      availableVector[j] = totalResources[j] - sumAllocated;
    }
  }

  public void generateRandomData() {
    Random rand = new Random();
    for (int i = 0; i < numResources; i++) totalResources[i] = rand.nextInt(16) + 5;
    int[] currentAllocated = new int[numResources];
    for (int i = 0; i < numProcesses; i++) {
      for (int j = 0; j < numResources; j++) {
        maxMatrix[i][j] = rand.nextInt(totalResources[j] + 1);
        int limit = maxMatrix[i][j];
        int remaining = totalResources[j] - currentAllocated[j];
        if (remaining < limit) limit = remaining;
        if (limit > 0) allocationMatrix[i][j] = rand.nextInt(limit + 1);
        else allocationMatrix[i][j] = 0;
        currentAllocated[j] += allocationMatrix[i][j];
      }
    }
    calculateInitialAvailable();
    calculateNeed();
  }

  public boolean readFromFile(String filename) {
    try {
      Scanner fileScanner = new Scanner(new File(filename));
      for (int i = 0; i < numProcesses; i++) for (int j = 0; j < numResources; j++) if (fileScanner.hasNextInt()) maxMatrix[i][j] = fileScanner.nextInt();
      for (int i = 0; i < numProcesses; i++) for (int j = 0; j < numResources; j++) if (fileScanner.hasNextInt()) allocationMatrix[i][j] = fileScanner.nextInt();
      for (int j = 0; j < numResources; j++) if (fileScanner.hasNextInt()) totalResources[j] = fileScanner.nextInt();
      fileScanner.close();
      calculateInitialAvailable();
      calculateNeed();
      for (int val : availableVector) if (val < 0) return false;
      return true;
    } catch (FileNotFoundException e) { return false; }
  }

  public void printState(int iteration) {
    if (iteration != 1) {
      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    System.out.println("\n\nIteration " + iteration + ":");
    System.out.println("------------");
    System.out.println("\n Claim Matrix:\n");
    System.out.print("   ");
    for (int i = 0; i < numResources; i++) System.out.print(" R" + i + " ");
    System.out.println();
    for (int i = 0; i < numProcesses; i++) {
      System.out.print("P" + i + " ");
      for (int j = 0; j < numResources; j++) System.out.printf("%3d ", maxMatrix[i][j]);
      System.out.println();
    }
    System.out.println("\n\n Allocation Matrix:\n");
    System.out.print("   ");
    for (int i = 0; i < numResources; i++) System.out.print(" R" + i + " ");
    System.out.println();
    for (int i = 0; i < numProcesses; i++) {
      System.out.print("P" + i + " ");
      for (int j = 0; j < numResources; j++) System.out.printf("%3d ", allocationMatrix[i][j]);
      System.out.println();
    }
    System.out.println("\n\n Needed Matrix:\n");
    System.out.print("   ");
    for (int i = 0; i < numResources; i++) System.out.print(" R" + i + " ");
    System.out.println();
    for (int i = 0; i < numProcesses; i++) {
      System.out.print("P" + i + " ");
      for (int j = 0; j < numResources; j++) System.out.printf("%3d ", needMatrix[i][j]);
      System.out.println();
    }
    System.out.println("\n\n Available vector:\n");
    for (int i = 0; i < numResources; i++) {
      System.out.print("R" + i + " = " + availableVector[i]);
      if (i < numResources - 1) System.out.print(", ");
    }
    System.out.println();
  }

  public void displayInitialData() {
    System.out.print("\nResources: ");
    for (int i = 0; i < numResources; i++) {
      System.out.print("R" + i + " = " + totalResources[i]);
      if (i < numResources - 1) System.out.print(", ");
    }
    System.out.print("\nProcesses: ");
    for (int i = 0; i < numProcesses; i++) {
      System.out.print("P" + i);
      if (i < numProcesses - 1) System.out.print(", ");
    }
    System.out.println();
  }

  public void run() {
    int iter = 1;
    int completed = 0;
    while (completed < numProcesses) {
      printState(iter);
      boolean found = false;
      for (int i = 0; i < numProcesses; i++) {
        if (!finish[i]) {
          boolean possible = true;
          for (int j = 0; j < numResources; j++) {
            if (needMatrix[i][j] > availableVector[j]) {
              possible = false;
              break;
            }
          }
          if (possible) {
            for (int j = 0; j < numResources; j++) {
              availableVector[j] += allocationMatrix[i][j];
              // ZERO OUT MATRICES FOR VISUALIZATION
              allocationMatrix[i][j] = 0;
              needMatrix[i][j] = 0;
              maxMatrix[i][j] = 0;
            }
            safeSequence.add(i);
            finish[i] = true;
            found = true;
            completed++;
            iter++;
            break; 
          }
        }
      }
      if (!found) {
        System.out.println("\nResource allocation is failed after " + iter + " iterations!");
        return;
      }
    }
    printState(iter); // Final print
    System.out.println("\nThe resource allocation has been completed within " + iter + " iterations!");
    System.out.println("The Safe-state order:");
    System.out.print("< ");
    for (int i = 0; i < safeSequence.size(); i++) {
      System.out.print("P" + safeSequence.get(i));
      if (i < safeSequence.size() - 1) System.out.print(", ");
    }
    System.out.println(" >");
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    String continueChoice;
    do {
      System.out.print("\n\n\tBanker's Algorithm for Multiple Resource Allocation");
      System.out.print("\n\t===================================================\n\n");
      System.out.print("Enter Resource size: ");
      int r = scanner.nextInt();
      System.out.print("Enter Process size: ");
      int p = scanner.nextInt();
      BankersAlgorithm banker = new BankersAlgorithm(r, p);
      System.out.print("Enter I to get input data from a file or press any key for random data: ");
      String inputType = scanner.next();
      if (inputType.equalsIgnoreCase("I")) {
        System.out.print("Enter the file name (with .txt extension): ");
        String filename = scanner.next();
        if (banker.readFromFile(filename)) System.out.println("\nFile opened successfully and data transfered into arrays.");
        else { System.out.println("Failed to read valid data. Exiting this run."); continueChoice = "Y"; scanner.nextLine(); continue; }
      } else {
        banker.generateRandomData();
        System.out.println("\nRandom data generated successfully.");
      }
      banker.displayInitialData();
      banker.run();
      System.out.print("\nEnter Y to continue or any key to Exit: ");
      continueChoice = scanner.next();
    } while (continueChoice.equalsIgnoreCase("Y"));
    scanner.close();
  }
}
