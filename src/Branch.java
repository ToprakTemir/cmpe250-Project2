import java.util.LinkedList;

public class Branch {
    public String city;
    public String district;
    public HashTable<Employee> employees;
    public int bonusGivenThisMonth;
    public int bonusTotalGiven; // total bonus given in this branch across all previous months
    public Employee manager;
    public LinkedList<Employee> managerPromotionQueue; // queue of cooks that are waiting to be promoted to manager
    public Employee canBePromotedToCook; // the cashier that is waiting to be promoted to cook. There can only be one, so it is not a list
    public int numOfCashiers;
    public int numOfCooks;
    public int numOfCouriers;
    public boolean managerWillBeDismissed;

    // if an employee except manager will be dismissed in the first chance, they are stored in the fields below.
    // the dismissals can only be delayed if there is only one employee, so these are not lists.
    public Employee toBeDismissedCook;
    public Employee toBeDismissedCashier;
    public Employee toBeDismissedCourier;
    public String name() {return city + " " + district;}
    public Branch(String cityName, String districtName) {
        city = cityName;
        district = districtName;
        employees = new HashTable<>();
        bonusGivenThisMonth = 0;
        bonusTotalGiven = 0;
        numOfCashiers = 0;
        numOfCooks = 0;
        numOfCouriers = 0;
        managerPromotionQueue = new LinkedList<>();
        managerWillBeDismissed = false;
    }
    public Employee get(String name) {
        return employees.get(name);
    }

    public void add(Employee employee) {
        employees.add(employee);
        switch (employee.position) {
            case "COURIER" -> {
                numOfCouriers++;
                if (toBeDismissedCourier != null){
                    Main.printWriter.println(toBeDismissedCourier.name + " is dismissed from branch: " + district + ".");
                    toBeDismissedCourier.dismiss();
                    toBeDismissedCourier = null;
                }
            }
            case "CASHIER" -> {
                numOfCashiers++;
                if (numOfCashiers == 2) { // if there is only one other cashier, check if they are waiting to be promoted/fired
                    if (canBePromotedToCook != null) {
                        canBePromotedToCook.promoteToCook();
                        canBePromotedToCook = null;
                    }
                    if (toBeDismissedCashier != null) {
                        Main.printWriter.println(toBeDismissedCashier.name + " is dismissed from branch: " + district + ".");
                        toBeDismissedCashier.dismiss();
                        toBeDismissedCashier = null;
                    }
                }
            }
            case "COOK" -> {
                numOfCooks++;
                if (employee.promotionPoints >= 10)
                    managerPromotionQueue.add(employee);
                if (numOfCooks == 2 && toBeDismissedCook != null) {
                    Main.printWriter.println(toBeDismissedCook.name + " is dismissed from branch: " + district + ".");
                    toBeDismissedCook.dismiss();
                    toBeDismissedCook = null;
                }
                checkForManagerPromotion(false);
            }
            case "MANAGER" -> // this line only happens during the initial insertion of employees
                manager = employee;
        }
    }

    /**
     * checks if a cook can be promoted to manager, and promotes them if it can
     * @return true if a cook was promoted to manager, false otherwise
     */
    public boolean checkForManagerPromotion(boolean managerWantsToLeave) {
        if ((managerWillBeDismissed || managerWantsToLeave) && !managerPromotionQueue.isEmpty() && numOfCooks > 1) { // fire and replace the manager
            if (managerWantsToLeave) Main.printWriter.println(manager.name + " is leaving from branch: " + district + ".");
            if (managerWillBeDismissed) Main.printWriter.println(manager.name + " is dismissed from branch: " + district + ".");
            employees.remove(manager.name);
            managerPromotionQueue.getFirst().promoteToManager();
            managerPromotionQueue.remove(managerPromotionQueue.getFirst());
            managerWillBeDismissed = false;
            return true;
        }
        return false;
    }

    public void handleLeaveRequest(String name) {
        Employee employee = get(name);
        switch(employee.position) {
            case "COURIER" -> {
                if (numOfCouriers == 1 && toBeDismissedCourier != employee)
                    employee.giveBonus(200);

                else if (numOfCouriers == 1) {} // if they can't leave but they're on dismissal list, do nothing

                else {
                    Main.printWriter.println(name + " is leaving from branch: " + district + ".");
                    employee.dismiss();
                }
            }
            case "CASHIER" -> {
                if (numOfCashiers == 1 && toBeDismissedCashier != employee)
                    employee.giveBonus(200);

                else if (numOfCashiers == 1) {} // if they can't leave but they're on dismissal list, do nothing

                else {
                    Main.printWriter.println(name + " is leaving from branch: " + district + ".");
                    employee.dismiss();
                }
            }
            case "COOK" -> {
                if (numOfCooks == 1 && toBeDismissedCook != employee)
                    employee.giveBonus(200);

                else if (numOfCooks == 1) {} // if they can't leave but they're on dismissal list, do nothing

                else {
                    Main.printWriter.println(name + " is leaving from branch: " + district + ".");
                    employee.dismiss();
                }
            }
            case "MANAGER" -> {
                if (!checkForManagerPromotion(true) && !managerWillBeDismissed)
                    employee.giveBonus(200);
            }
        }
    }

    /**
     * 
     * @param employee: the employee that we're updating
     */
    public void updateSituation(Employee employee) {
        switch(employee.position) {
            case "COURIER" -> {
                if (toBeDismissedCourier == employee && numOfCouriers > 1) {
                    Main.printWriter.println(employee.name + " is dismissed from branch: " + district + ".");
                    employee.dismiss();
                    toBeDismissedCourier = null;
                }
            }
            case "CASHIER" -> {
                if (toBeDismissedCashier == employee && numOfCashiers > 1) {
                    Main.printWriter.println(employee.name + " is dismissed from branch: " + district + ".");
                    employee.dismiss();
                    toBeDismissedCashier = null;
                }
                if (canBePromotedToCook == employee && numOfCashiers > 1) {
                    employee.promoteToCook();
                    canBePromotedToCook = null;
                }
            }
            case "COOK" -> {
                if (toBeDismissedCook == employee && numOfCooks > 1) {
                    Main.printWriter.println(employee.name + " is dismissed from branch: " + district + ".");
                    employee.dismiss();
                    toBeDismissedCook = null;
                }
                checkForManagerPromotion(false);
            }
            case "MANAGER" -> {
                if (managerWillBeDismissed)
                    checkForManagerPromotion(false);
            }
        }
    }
}
