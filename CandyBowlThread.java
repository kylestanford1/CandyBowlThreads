/*
@Author: KYLE STANFORD
@Date: April 20th, 2022
@Purpose: Create Threads to consume 'candy' and one to fill the bowl 
	    up when it is empty.
*/


import java.util.Random;
import java.util.Scanner;

class Bowl {
    private int PieceKnt, MaxPieces;
    Bowl(int MaxPieces) {
        this.MaxPieces = MaxPieces;
        PieceKnt = MaxPieces;
    }

    //Get candy
    synchronized public boolean Get() {
        if (PieceKnt > 0) {
            PieceKnt--;
            System.out.println("The bowl has " + PieceKnt + " pieces of candy left");
            notifyAll();
            return true;
        }
        else {
            notifyAll();
            return false;
        }
    }
    //Fills bowl back up
    synchronized public void Fill() {
        PieceKnt = MaxPieces;
        System.out.println("The bowl is full.");
        notifyAll();
    }
}

//Professor class
class Professor extends Thread {
    private Bowl[] BowlList;
    private TA[] TAList;
    private String name;
    private Random rand = new Random();
    private int numCandy, sleep, SleepTime, target, BowlKnt, TAKnt;
    private boolean GetStatus;

    Professor(Bowl[] BowlList, TA[] TAList, int sleep, String name) {
        //Setting everything up
        numCandy = 0;
        this.TAList = TAList;
        TAKnt = 1;
        this.sleep = sleep;
        this.name = name;
        this.BowlList = BowlList;
        BowlKnt = BowlList.length;


    }

    //Run method (override)
    public void run() {
        try {
            //loop of eating and sleeping
            while (true) {
                target = rand.nextInt(BowlKnt);
                GetStatus = BowlList[target].Get();
                if (GetStatus == false) {
                    //Bowl empty, fill
                    TAList[rand.nextInt(TAKnt)].FillBowl(target);
                }
                else {
                    numCandy++;
                    //Sleep
                    SleepTime = rand.nextInt(sleep);
                    System.out.println(name + " has consumed a candy and sleeps for " + SleepTime + "ms.");
                    Thread.sleep(SleepTime);
                }
            }
        }
        catch (InterruptedException e) {
        }
        //print
        finally {
            System.out.println(name + " ate " + numCandy + " pieces of candy.");
        }
    }
}

class TA {
    //Vars
    private Bowl[] BowlList;

    //Construct TA
    TA(Bowl[] BowlList) {
        this.BowlList = BowlList;
    }

    //Fillbowl method
    public void FillBowl(int target) {
        BowlList[target].Fill();
        System.out.println("The TA has filled the candy bowl!");
    }
}

//Main
public class CandyBowl {

    private static Scanner input = new Scanner(System.in);

    public static void main(String[]args) {

        //Method Call
        EnumParams();
    }

    //Choose what you want
    private static void EnumParams() {
        int numProf, BowlKnt, PieceKnt;
        String Name;
        BowlKnt = 1;

        //INPUT
        System.out.print("Enter number of professors: ");
        numProf = input.nextInt();

        System.out.print("Enter how much candy the bowl contains: ");
        PieceKnt = input.nextInt();


        //Makes the amount of everything the user put in
        Bowl[] Bowls = new Bowl[BowlKnt];
        for (int i = 0; i < BowlKnt; i++) {
            Bowls[i] = new Bowl(PieceKnt);
        }

        TA[] TAs = new TA[1];
        TAs[0] = new TA(Bowls);


        Professor[] Professors = new Professor[numProf];
        for (int i = 0; i < numProf; i++) {
            Name = "Professor" + i;
            Professors[i] = new Professor(Bowls, TAs, 20, Name);
        }

        System.out.println("Starting\n\n");
        for (int i = 0; i < numProf; i++) {
            Professors[i].start();
        }

    }

}
