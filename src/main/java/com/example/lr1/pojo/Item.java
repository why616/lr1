package com.example.lr1.pojo;

public class Item {
    public String first;
    public String create;
    public String[] look;
    public int dotpos;
    public boolean ifJ;

    public Item(String first, String create, String[] look, int dotpos,boolean ifJ) {
        this.first = first;
        this.create = create;
        this.look = look;
        this.dotpos = dotpos;
        this.ifJ = ifJ;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String[] getLook() {
        return look;
    }

    public void setLook(String[] look) {
        this.look = look;
    }

    public int getDotpos() {
        return dotpos;
    }

    public void setDotpos(int dotpos) {
        this.dotpos = dotpos;
    }

    public boolean ifSame(Item item){
        if (item.look.length==this.look.length){
            for (int i =0;i<this.look.length;i++){
                boolean flag = false;
                for (int j = 0; j<item.look.length;j++){
                    if (this.look[i]==null||item.look[j]==null)
                        continue;
                    try {
                        if (this.look[i].equals(item.look[j])){
                            flag = true;
                            break;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println(this.look[i]);
                        System.out.println(item.look[j]);
                    }

                }
                if (!flag){
                    return false;
                }
            }
            if ((item.first.equals(this.first))&&(item.create.equals(this.create))&&(item.dotpos==this.dotpos))
                return true;
        }
        return false;
    }
}
