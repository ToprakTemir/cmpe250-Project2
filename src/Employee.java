public class Employee {
    public String name;
    public String position; // it can be one of the following: "COURIER", "CASHIER", "COOK", "MANAGER"
    public Branch getBranch;
    public int promotionPoints = 0;

    public Employee(String name, String position, Branch branch) {
        this.name = name;
        this.position = position;
        this.getBranch = branch;
    }

    public void promoteToCook() {
        this.dismiss();
        position = "COOK";
        promotionPoints -= 3;
        getBranch.add(this);
        Main.printWriter.println(name + " is promoted from Cashier to Cook.");
    }

    public void promoteToManager() {
        promotionPoints -= 10;
        position = "MANAGER";
        getBranch.numOfCooks--;
        getBranch.manager = this;
        Main.printWriter.println(name + " is promoted from Cook to Manager.");
    }

    /**
     * dismisses an employee without checking if they should be dismissed. Should only be used when it is
     * known that employee should be dismissed.
     * note: This method shouldn't be used to dismiss a manager.
     */
    public void dismiss() {
        getBranch.employees.remove(this.name);
        switch (position) {
            case "COURIER" -> {
                getBranch.numOfCouriers--;
                if (getBranch.toBeDismissedCourier == this)
                    getBranch.toBeDismissedCourier = null;
            }
            case "CASHIER" -> {
                getBranch.numOfCashiers--;
                if (getBranch.toBeDismissedCashier == this)
                    getBranch.toBeDismissedCashier = null;
                if (getBranch.canBePromotedToCook == this)
                    getBranch.canBePromotedToCook = null;
            }
            case "COOK" -> {
                getBranch.numOfCooks--;
                if (getBranch.toBeDismissedCook == this)
                    getBranch.toBeDismissedCook = null;
                if (promotionPoints >= 10) {
                    getBranch.managerPromotionQueue.remove(this);
                }
            }
        }
    }
    public void giveBonus(int bonusAmount) {
        getBranch.bonusGivenThisMonth += bonusAmount;
        getBranch.bonusTotalGiven += bonusAmount;
    }

    /**
     * Updates the employee's promotionPoints and monthly bonus
     * Also checks if the employee should be in any watchlist after the change
     * @param score monthly score that will be added to that employer
     */
    public void addScore(int score) {
        if (score > 0) {
            promotionPoints += score / 200;
            giveBonus(score % 200);
        }
        else if (score < 0) {
            score = score * -1;
            promotionPoints -= score / 200;
        }
        switch (this.position) {
            case "COURIER" -> {
                if (promotionPoints <= -5)
                    getBranch.toBeDismissedCourier = this;
                else if (getBranch.toBeDismissedCourier == this) // if it was going to be dismissed but not anymore, remove from watch
                    getBranch.toBeDismissedCourier = null;
            }
            case "CASHIER" -> {
                if (promotionPoints <= -5)
                    getBranch.toBeDismissedCashier = this;
                else if (getBranch.toBeDismissedCashier == this) // if it was going to be dismissed but not anymore, remove from watch
                    getBranch.toBeDismissedCashier = null;
                else if (promotionPoints >= 3)
                    getBranch.canBePromotedToCook = this;
                else if (getBranch.canBePromotedToCook == this) // if it was going to be promoted but not anymore, remove from watch
                    getBranch.canBePromotedToCook = null;
            }
            case "COOK" -> {
                if (promotionPoints <= -5)
                    getBranch.toBeDismissedCook = this;
                else if (getBranch.toBeDismissedCook == this) // if it was going to be dismissed but not anymore, remove from watch
                    getBranch.toBeDismissedCook = null;
                else if (promotionPoints >= 10 && !getBranch.managerPromotionQueue.contains(this))
                    getBranch.managerPromotionQueue.add(this);
                else if (promotionPoints < 10) // if it was going to be promoted but not anymore, remove from watch
                    getBranch.managerPromotionQueue.remove(this);
            }
            case "MANAGER" -> {
                if (promotionPoints <= -5)
                    getBranch.managerWillBeDismissed = true;
                if (promotionPoints > -5)
                    getBranch.managerWillBeDismissed = false;
            }
        }
    }
}
