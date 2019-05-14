package top.longsh1z.www.mycat.model;

/**
 * Created by LongSh1z on 2019/5/3.
 */

public class CatFood {

    private String type;
    private int number;

    public CatFood(String type, int number){
        this.type = type;
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
