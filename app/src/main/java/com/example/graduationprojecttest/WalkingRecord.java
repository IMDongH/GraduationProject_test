package com.example.graduationprojecttest;

import java.util.ArrayList;
import java.util.List;

public class WalkingRecord {

    ArrayList<WalkingDTO> record = new ArrayList<>();

    public void addRecord(WalkingDTO walking){
        record.add(walking);
    }

    public ArrayList<WalkingDTO> getRecord() {
        return record;
    }
}


