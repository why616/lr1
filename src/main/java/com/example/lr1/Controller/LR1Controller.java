package com.example.lr1.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.lr1.pojo.Item;
import com.example.lr1.pojo.SetToStringArray;
import com.example.lr1.pojo.State;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.*;
import java.util.*;

@RestController
public class LR1Controller {
    int stateNum = 0;
    Map<Integer,Map<String,String>> ActionMap = new HashMap<>();
    Map<Integer,Map<String,Integer>> GotoMap = new HashMap<>();
    Map<String,Integer> createOrder = new HashMap<>();
    Map<Integer,String> orderCreat = new HashMap<>();
    Map<String,String> createAndE = new HashMap<>();

    Map<String,Set<String>> FM = new HashMap<>();
    boolean flag = true;
    boolean ifAllNull = false;
    Map<String, Set<String>> first = null;

    @RequestMapping("/lr1Analyze")
    public Map<String, Object> lr1Analyze(@RequestParam String data, @RequestParam String inString){
        Map<String,String> G = new HashMap<>();
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String,Object> map1 = new HashMap<>();

        Object parse = JSONArray.parse(data);
        String b =data;
        JSONArray jsonArray = (JSONArray)parse;
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()){
            JSONObject jsonObject = (JSONObject) iterator.next();
            String key = (String) jsonObject.get("value");
            jsonObject = (JSONObject) iterator.next();
            String value = (String) jsonObject.get("value");
            if (!(key.equals("")||value.equals("")))
                G.put(key,value);
        }
        first = creatFirst(G);
        createActionAndGoto(G);
        Set<String> Aset = new HashSet<>();
        for (Integer integer : ActionMap.keySet()) {
           Aset.addAll(ActionMap.get(integer).keySet());
        }
        Set<String> Gset = new HashSet<>();
        for (Integer integer : GotoMap.keySet()) {
            Gset.addAll(GotoMap.get(integer).keySet());
        }

        StringBuffer stringBuffer = new StringBuffer(inString);
        stringBuffer.append("#");
        Stack<Integer> stateS = new Stack<>();
        Stack<String> charS = new Stack<>();
        stateS.push(0);
        StringBuffer action = new StringBuffer();
        action.append("初始化");
        charS.push("#");

        int time = 0;
        while (true){
            Iterator<Integer> iterator1 = stateS.iterator();
            Iterator<String> iterator2 = charS.iterator();
            StringBuffer stateString = new StringBuffer();
            StringBuffer charString = new StringBuffer();
            Map<String,Object> map = new HashMap<>();
            while (iterator1.hasNext())
                stateString.append(iterator1.next());
            while (iterator2.hasNext())
                charString.append(iterator2.next());

            System.out.print(time+"         "+stateString+"       "+charString+"       "+stringBuffer+"       ");
            map.put("order",time);
            map.put("stateStack",stateString);
            map.put("charStack",charString);
            map.put("inString",stringBuffer.toString());

            time++;
            action = new StringBuffer();
            String conmamd = ActionMap.get(stateS.peek()).get(String.valueOf(stringBuffer.charAt(0)));
            switch (conmamd.charAt(0)){
                case 'r':
                    action.append(conmamd+":");
                    String string = orderCreat.get(Integer.parseInt(String.valueOf(conmamd.charAt(1))));
                    for (int i = 0;i<string.length();i++){
                        stateS.pop();
                        charS.pop();
                    }
                    int num = stateS.peek();
                    stateS.push(GotoMap.get(stateS.peek()).get(createAndE.get(string)));
                    charS.push(createAndE.get(string));
                    action.append(charS.peek()+"->"+string+"归约,GOTO("+String.valueOf(num)+","+charS.peek()+")="+stateS.peek()+"入栈");
                    break;
                case 's':
                    action.append("ACTION["+stateS.peek()+","+String.valueOf(stringBuffer.charAt(0))+"]="+conmamd+"状态"+String.valueOf(conmamd.charAt(1))+"入栈");
                    stateS.push(Integer.parseInt(String.valueOf(conmamd.charAt(1))));
                    charS.push(String.valueOf(stringBuffer.charAt(0)));
                    stringBuffer.deleteCharAt(0);

                    break;
                default:
                    if (conmamd.equals("acc")){
                        action.append("ACC: 分析成功") ;
                        map.put("action",action.toString());
                        result.add(map);
                        map1.put("result",result);
                        map1.put("statement","Accept!");
                        map1.put("ActionSet",Aset);
                        map1.put("GotoSet",Gset);
                        map1.put("ActionMap",ActionMap);
                        map1.put("GotoMap",GotoMap);
                        System.out.println(action);
                        System.out.println("Accept!");
                        back();
                        return map1;
                    }
            }
            map.put("action",action.toString());
            result.add(map);
            System.out.println(action);
        }
//        return "TBD";
    }

    public void createActionAndGoto(Map<String,String> G){
        List<State> stateList = new ArrayList<>();
        int order = 0;
        for (String key : G.keySet()) {
            for (String create : G.get(key).split("\\|")) {
                createOrder.put(create,order);
                orderCreat.put(order,create);
                createAndE.put(create,key);
                order++;
            }
        }
        State state = new State(stateNum);
        Item item3 = new Item("Z", "S", new String[]{"#"}, 0, true);
        state.addJ(item3);
        state.addItem(item3);
        ActionMap.put(stateNum,new HashMap<>());
        GotoMap.put(stateNum,new HashMap<>());
        stateNum++;
        stateList.add(state);
        for (int i = 0 ;i<stateList.size();i++){
            //求closure，给状态加项目
            for (int j = 0;j<stateList.get(i).itemList.size();j++){
                Item item = stateList.get(i).itemList.get(j);
                if (item.ifJ) {
                    stateList.get(i).addJ(item);
                }
                if (item.dotpos < item.create.length())
                    if (item.create.charAt(item.dotpos) >= 65 && item.create.charAt(item.dotpos) <= 90) {
                        String[] look;
                        if ((item.dotpos + 1) >= item.create.length()) {
                            look = item.look;
                        } else {
                            if (!(item.create.charAt(item.dotpos + 1) >= 65 && item.create.charAt(item.dotpos + 1) <= 90)) {
                                look = new String[]{String.valueOf(item.create.charAt(item.dotpos + 1))};
                            } else
                                look = SetToStringArray.change(first.get(String.valueOf(item.create.charAt(item.dotpos + 1))));
                        }
                        for (String create : G.get(String.valueOf(item.create.charAt(item.dotpos))).split("\\|")) {
                            Item item1 = new Item(String.valueOf(item.create.charAt(item.dotpos)), create, look, 0, false);
                            //解决项目重复添加问题
                            boolean flag = false;
                            for (Item item2 : stateList.get(i).itemList) {
                                    if (item1.ifSame(item2)) {
                                        flag = true;
                                        break;
                                    }
                            }
                            if (!flag) {
                                stateList.get(i).addItem(item1);
                            }
                        }
                    }
            }
            //根据每个项目，创建新的state
                    Set<Character> dotposCharSet = new HashSet<>();
                    for (Item item1 : stateList.get(i).itemList) {
                        if (item1.dotpos==item1.create.length()) {
                            for (String string : item1.look) {
                                if (item1.first.equals("Z"))
                                    ActionMap.get(stateList.get(i).StateNum).put(string, "acc");
                                else
                                ActionMap.get(stateList.get(i).StateNum).put(string, "r" + createOrder.get(item1.create));
                            }
                        }
                        else
                        dotposCharSet.add(item1.create.charAt(item1.dotpos));
                    }
                    for (Character character : dotposCharSet) {
                        Set<Item> tempJ = new HashSet<>();
                        for (Item item01 : stateList.get(i).itemList) {
                            if (item01.dotpos!=item01.create.length())
                            if (character == item01.create.charAt(item01.dotpos)) {
                                tempJ.add(new Item(item01.first,item01.create,item01.look,item01.dotpos+1,true));
                            }
                        }
                        flag = false;
                        boolean ifNew = false;
                        for (State state2 : stateList) {
                            if (state2.J.size() == tempJ.size()) {
                                for (Item item1 : state2.J) {
                                    flag = false;
                                    for (Item item2 : tempJ) {
                                        if (item2.ifSame(item1)) {
                                            flag = true;
                                            break;
                                        }
                                    }
                                    if (!flag) {
                                        break;
                                    }
                                }
                                //找到了J相同的，无需新加状态
                                if (flag) {
                                    if ((character >= 65) && (character <= 90)) {
                                        GotoMap.get(stateList.get(i).StateNum).put(String.valueOf(character), state2.StateNum);
                                    } else {
                                        ActionMap.get(stateList.get(i).StateNum).put(String.valueOf(character), "s" + String.valueOf(state2.StateNum));
                                    }
                                    stateList.get(i).I0SI1.put(String.valueOf(character), state2.StateNum);
                                    ifNew = true;
                                    break;
                                } else {
                                    //没找到相同的J
                                }
                            }
                        }
                        //添加新状态
                        if (!ifNew) {
                            stateList.add(new State(stateNum, tempJ));
                            ActionMap.put(stateNum,new HashMap<>());
                            GotoMap.put(stateNum,new HashMap<>());
                            if ((character >= 65) && (character <= 90)) {
                                GotoMap.get(stateList.get(i).StateNum).put(String.valueOf(character), stateNum);
                            } else {
                                ActionMap.get(stateList.get(i).StateNum).put(String.valueOf(character), "s" + String.valueOf(stateNum));
                            }
                            stateNum++;
                        }
                    }
                }
        }
    public Map<String,Set<String>> creatFirst(Map<String,String> G){
        Set<String> keySet = G.keySet();
        Iterator<String> iterator = keySet.iterator();
        Map<String,Set<String>> first = new HashMap<>();
        while (iterator.hasNext()){
            String E = iterator.next();

            Set<String> setTemp= new HashSet<>();
            first.put(E,setTemp);

            String string = G.get(E);
            String[] strings = string.split("\\|");
            for (String string1:strings) {
                Set<String> setTemp1= new HashSet<>();
                FM.put(string1,setTemp1);
            }
        }

        while (flag){
            flag = false;
            iterator=keySet.iterator();
            while (iterator.hasNext()){//遍历每个E->.... 的E
                String E = iterator.next();
//                Map<String,String> temp = M.get(E);
                String string = G.get(E);
                String[] strings = string.split("\\|");

                for (String string1:strings) {
                    Set<String> setTemp1= FM.get(string1);
                    FM.put(string1,setTemp1);
                    if (string1.charAt(0)>=65&&string1.charAt(0)<=90){
                        try {
                            Set<String> set = first.get(String.valueOf(string1.charAt(0)));
                            set.forEach(firstOver->{//查找该候选式开头的非终结符的first集，firsr集中如果有新的终结符，就把新（新终结符，候选式）加入预测分析表M，新终结符加入first E
                                if (!firstOver.equals("~")) {
                                    boolean ifNew = first.get(E).add(firstOver);
                                    setTemp1.add(firstOver);
                                    if (ifNew){
//                                        temp.put(firstOver,string1);

                                        flag = true;
                                    }
                                }

                            });
                        }catch (NullPointerException e){
                            e.printStackTrace();
                            System.out.println("空指针说明还没建立first");
                        }
                        if (first.get(String.valueOf(string1.charAt(0))).contains("~")){
                            ifAllNull = false;
                            for (int i = 1; i<string1.length();i++){

                                if (!(string1.charAt(i)>=65&&string1.charAt(i)<=90)){
                                    first.get(E).add(String.valueOf(string1.charAt(i)));
                                    setTemp1.add(String.valueOf(string1.charAt(i)));
                                    break;
                                }
                                else {
                                    ifAllNull = true;
                                    Set<String> set = first.get(String.valueOf(string1.charAt(i)));
                                    set.forEach(firstOver->{//查找该候选式开头的非终结符的first集，firsr集中如果有新的终结符，就把新（新终结符，候选式）加入预测分析表M，新终结符加入first E
                                        boolean ifNew = first.get(E).add(firstOver);
                                        setTemp1.add(firstOver);
                                        if (ifNew){
                                            if (firstOver!="~")
//                                                temp.put(firstOver,string1);

                                            flag = true;
                                        }
                                    });
                                    if (!first.get(String.valueOf(string1.charAt(i))).contains("~")){
                                        ifAllNull = false;
                                        break;
                                    }
                                }
                            }
                        }
                        if (ifAllNull){
                            first.get(E).add("~");
                            setTemp1.add("~");
                        }
                    }else {

                        boolean ifNew = first.get(E).add(String.valueOf(string1.charAt(0)));
                        setTemp1.add(String.valueOf(string1.charAt(0)));
                        if (ifNew){
                            if (!String.valueOf(string1.charAt(0)).equals("~")){

                            }
//                                temp.put(String.valueOf(string1.charAt(0)),string1);
                            flag = true;
                        }
                    }
                }
            }
        }
        return first;
    }
    public void back(){
      stateNum = 0;
        ActionMap = new HashMap<>();
       GotoMap = new HashMap<>();
        createOrder = new HashMap<>();
        orderCreat = new HashMap<>();
        createAndE = new HashMap<>();
        FM = new HashMap<>();
       flag = true;
       ifAllNull = false;
        first = null;
    }
}
