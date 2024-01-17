import java.util.ArrayList;
import java.util.LinkedList;

import static java.lang.Math.sqrt;

public class HashTable<E> {
    private int tableSize;
    public ArrayList<LinkedList<E>> table;
    private int numberOfElements;
    private static final double ALLOWED_LOAD_LIMIT = 2;

    public HashTable() {
        this(31);
    }
    private HashTable(int initialSize) {
        tableSize = initialSize;
        table = new ArrayList<>(tableSize);
        while (table.size() < tableSize) table.add(null);
        numberOfElements = 0;
    }

    /**
     * @param name : name of the wanted element
     * @return the element with the same name as the argument
     */
    public E get(String name) {
        int hashCode = name.hashCode() % tableSize; if (hashCode < 0) hashCode += tableSize;
        LinkedList<E> l = this.table.get(hashCode); // the linkedList at position hashCode
        if (l == null) return null;
        for (E element : l) {
            if (element instanceof Employee){
                if (((Employee)element).name.equals(name)) return element;
            }
            else if (element instanceof Branch){
                if (((Branch)element).name().equals(name)) return element;
            }
        }
        return null;
    }
    private double loadFactor() {
        return ((double)numberOfElements) / tableSize;
    }

    private void add(E element, String name) {
        int hashCode = name.hashCode() % tableSize; if (hashCode < 0) hashCode += tableSize;
        if (table.get(hashCode) == null) table.set(hashCode, new LinkedList<>()); // if that position is null, insert empty list
        table.get(hashCode).add(element);
        numberOfElements++;
        if (loadFactor() > ALLOWED_LOAD_LIMIT) rehash();
    }

    public void add(E element) {
        String name = "EMPTY";
        if (element instanceof Branch) name = ((Branch)element).name();
        else if (element instanceof Employee) name = ((Employee)element).name;
        add(element, name);
    }

    public void remove(String name) {
        int hashCode = name.hashCode() % tableSize; if (hashCode < 0) hashCode += tableSize;
        boolean successfullyRemoved = false;
        E elementToBeRemoved = table.get(hashCode).getFirst(); // temp value for now
        for (E element : table.get(hashCode)) { // iterate over the linkedList at index
            if (element instanceof Employee){
                if (((Employee)element).name.equals(name)) {
                    elementToBeRemoved = element;
                    numberOfElements--;
                    successfullyRemoved = true;
                }
            }
            else if (element instanceof Branch) {
                if (((Branch)element).name().equals(name)) {
                    elementToBeRemoved = element;
                    numberOfElements--;
                    successfullyRemoved = true;
                }
            }
        }
        table.get(hashCode).remove(elementToBeRemoved);
        if (!successfullyRemoved) System.out.println("hashTable remove operation was failed");
    }


    private void rehash() {
        tableSize = nextPrime(2*tableSize);
        ArrayList<LinkedList<E>> newTable = new ArrayList<>();
        while (newTable.size() < tableSize) newTable.add(null);
        ArrayList<LinkedList<E>> oldTable = table;
        table = newTable;
        for (LinkedList<E> elementList : oldTable) {
            if (elementList == null) continue;
            for (E element : elementList) {
                add(element);
            }
        }
    }

    /**
     * returns the smallest prime number that is bigger than the input number
     */
    private static int nextPrime(int num) {
        int p = num - 1;
        boolean isPrime;
        do {
            isPrime = true;
            p++;
            for (int i=2; i <= sqrt(p); i++)
                if (p % i == 0) {
                    isPrime = false;
                    break;
                }
        } while (!isPrime);
        return p;
    }
}
