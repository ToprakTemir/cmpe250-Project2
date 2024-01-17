import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static PrintWriter printWriter;
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java Project2 <inputfile1> <inputfile2> <outputfile>");
            System.exit(1);
        }

        String inputFile1Name = args[0];
        String inputFile2Name = args[1];
        String outputFileName = args[2];

        File initialInputFile = new File(inputFile1Name);
        File inputFile = new File(inputFile2Name);
        Scanner initialInputReader = new Scanner(initialInputFile);
        Scanner inputReader = new Scanner(inputFile);

        FileWriter fileWriter = new FileWriter(outputFileName, false);
        printWriter = new PrintWriter(fileWriter);

        HashTable<Branch> branches = new HashTable<>();

        while (initialInputReader.hasNext()) {
            String[] curLine = initialInputReader.nextLine().split(",");
            String branchName = curLine[0].trim() + " " + curLine[1].trim();
            if (branches.get(branchName) == null) branches.add(new Branch(curLine[0].trim(), curLine[1].trim()));
            branches.get(branchName).add(new Employee(curLine[2].trim(), curLine[3].trim(), branches.get(branchName)));
        }

        while (inputReader.hasNext()) {

            String[] curLine = inputReader.nextLine().split(": ");

            // if this is a month changer line, reset every branch's monthly bonus counter
            if (curLine.length == 1) {
                for (LinkedList<Branch> branchLinkedList : branches.table) {
                    if (branchLinkedList == null) continue;
                    for (Branch branch : branchLinkedList) branch.bonusGivenThisMonth = 0;
                }
                continue;
            }

            String request = curLine[0];
            curLine = curLine[1].split(","); // rest of the line after request
            String curBranchName = curLine[0].trim() + " " + curLine[1].trim();
            Branch curBranch = branches.get(curBranchName);

            String name;      // name of the employee we'll be dealing in the input
            String position;  // position of the employee we'll be dealing in the input
            switch (request) {
                case "ADD" -> {
                    name = curLine[2].trim();
                    position = curLine[3].trim();
                    if (curBranch.get(name) != null) {
                        printWriter.println("Existing employee cannot be added again.");
                        continue;
                    }
                    curBranch.add(new Employee(name, position, curBranch));
                }
                case "LEAVE" -> {
                    name = curLine[2].trim();
                    if (curBranch.get(name) == null) {
                        printWriter.println("There is no such employee.");
                        continue;
                    }
                    curBranch.handleLeaveRequest(name);
                }
                case "PERFORMANCE_UPDATE" -> {
                    name = curLine[2].trim();
                    Employee employee = curBranch.get(name);
                    if (employee == null) {
                        printWriter.println("There is no such employee.");
                        continue;
                    }
                    int score = Integer.parseInt(curLine[3].trim());
                    employee.addScore(score);
                    curBranch.updateSituation(employee);
                }
                case "PRINT_MONTHLY_BONUSES" ->
                        printWriter.println("Total bonuses for the " + curBranch.district + " branch this month are: " + curBranch.bonusGivenThisMonth);
                case "PRINT_OVERALL_BONUSES" ->
                        printWriter.println("Total bonuses for the " + curBranch.district + " branch are: " + curBranch.bonusTotalGiven);
                case "PRINT_MANAGER" ->
                        printWriter.println("Manager of the " + curBranch.district + " branch is " + curBranch.manager.name + ".");
            }
        }
        printWriter.close();
    }
}