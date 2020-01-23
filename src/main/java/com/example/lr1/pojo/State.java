package com.example.lr1.pojo;

import java.util.*;

public class State {
   public int StateNum;
   public List<Item> itemList = new ArrayList<>();
   public Map<String,Integer> I0SI1 = new HashMap<>();
   public Set<Item> J = new HashSet<>();

    public State(int stateNum) {
        StateNum = stateNum;
    }

    public State(int stateNum, Set<Item> J) {
        this.StateNum = stateNum;
        this.J = J;
        for (Item item : J) {
            itemList.add(item);
        }
    }

    public void addItem(Item item){
        itemList.add(item);
    }
    public void addJ(Item item){J.add(item);}
}
