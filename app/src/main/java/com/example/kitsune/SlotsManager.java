package com.example.kitsune;

import android.content.Context;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;

public class SlotsManager {
    private final String[][] allSlots = {
            {"TF1", "TA1", "E1-STC2", "D1", "B1", "LUNCH", "TA2", "E2-STC1", "D2", "B2", "TF2"},
            {"TCC1", "E1-STA2", "G1-TFF1", "TBB1", "TDD1", "LUNCH", "E2-STA1", "G2-TFF2", "TBB2", "TDD2", "TCC2"},
            {"TE1", "C1", "A1", "F1", "D1", "LUNCH", "C2", "A2", "F2", "D2", "TE2"},
            {"TAA1", "TD1", "B1", "G1-TEE1", "C1", "LUNCH", "TD2", "B2", "G2-TEE2", "C2", "TAA2"},
            {"TG1", "TB1", "TC1", "A1", "F1", "LUNCH", "TB2", "TC2", "A2", "F2", "TG2"}};
    private final String[][] labSlots = {
            {"L1", "L2", "L3", "L4", "L5", "LUNCH", "L31", "L32", "L33", "L34", "L35"},
            {"L7", "L8", "L9", "L10", "L11", "LUNCH", "L37", "L38", "L39", "L40", "L41"},
            {"L13", "L14", "L15", "L16", "L17", "LUNCH", "L43", "L44", "L45", "L46", "L47"},
            {"L19", "L20", "L21", "L22", "L23", "LUNCH", "L49", "L50", "L51", "L52", "L53"},
            {"L25", "L26", "L27", "L28", "L29", "LUNCH", "L55", "L56", "L57", "L58", "L59"},
    };
    Calendar calendar = Calendar.getInstance();

    /**
     * returns the day of the week in int.
     * 1- sunday, 7- saturday
     */
    public int getDayOfWeek() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * index 0 = an array of slots of tuesday.
     * index 4 = an array of slots of saturday.
     *
     * @return An 2D array. Each element in the array is an array of slots for that day.
     */
    public String[][] getAllSlots() {
        return allSlots;
    }

    /**
     * index 0 = an array of slots of tuesday.
     * index 4 = an array of slots of saturday.
     *
     * @return An 2D array. Each element in the array is an array of slots for that day.
     */
    public String[][] getLabSlots() {
        return labSlots;
    }


    /**
     *
     * @param context
     * @return a String of all upcoming classes separated by new line. example: "PHY1008 AB1 303, AT 9:00 AM \n ECE1002 AB1 310, AT 10:00 AM"
     */
    public String getUpcomingClass(Context context){
        int day = this.getDayOfWeek();
        if(day < 3) return "No upcoming classes.";
        day -= 3;
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        if(hour > 18) return "No upcoming classes.";
        hour -= 7;
        String[] slots = allSlots[day];
        String[] labSlots = getLabSlots()[day];
        DBManager db = new DBManager(context);
        String[] enrolled = db.getData();
        if(enrolled.length == 0) return "-1";
        int[] enrolledETH = classesForToday(enrolled,slots);
        int[] enrolledEla = classesForToday(enrolled,labSlots);
        String result = "";
        for (int i = hour; i < enrolledETH.length;i++){
            int t = i+8;
            String time = t + ":00 AM";
            if(t>12) {
                t -= 12;
                time = t + ":00 PM";
            }

            if(enrolledETH[i] == -1){
                if( enrolledEla[i] == -1) continue;
                String[] r = enrolled[enrolledEla[i]].split("-");
                String[] arr = Arrays.copyOfRange(r, 1, r.length);
                String toadd = String.join(" ", arr);
                result += "LAB: " + toadd+", At "+ time+ "\n";
            }
            else {
                String[] r = enrolled[enrolledETH[i]].split("-");
                String[] arr = Arrays.copyOfRange(r, 1, r.length);
                String toadd = String.join(" ", arr);
                result += toadd + ", At " + time + "\n";
            }
        }
        return result.isEmpty() ? "No upcoming classes for today." : result;
    }

    public int[] classesForToday(String[] enrolled,String[] slots){
        int[] result = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        for(int i = 0; i < slots.length;i++){
           result[i] = checkForSlot(enrolled,slots[i]);
        }
        return result;
    }

    public int checkForSlot(String[] enrolled,String slot){
        if(slot.equalsIgnoreCase("LUNCH")) return -1;
        if(slot.contains("-")) return checkForSlot(enrolled,slot.split("-"));
        for(int i = 0; i < enrolled.length;i++){
            if(enrolled[i].split("-")[0].equalsIgnoreCase(slot)) return i;
        }
        return -1;
    }
    public int checkForSlot(String[] enrolled,String[] slot){
        for(int i = 0; i < enrolled.length;i++){
            if(enrolled[i].split("-")[0].equalsIgnoreCase(slot[0]) || enrolled[i].split("-")[0].equalsIgnoreCase(slot[1])) return i;
        }
        return -1;
    }
}
