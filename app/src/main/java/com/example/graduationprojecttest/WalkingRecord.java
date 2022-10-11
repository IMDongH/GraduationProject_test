package com.example.graduationprojecttest;

import java.util.ArrayList;
import java.util.List;

public class WalkingRecord {

    List<WalkingDTO> record = new ArrayList<>();

    public void addRecord(WalkingDTO walking){
        record.add(walking);
    }

    public List<WalkingDTO> getRecord() {
        return record;
    }
}


