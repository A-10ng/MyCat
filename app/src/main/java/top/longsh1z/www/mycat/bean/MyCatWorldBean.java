package top.longsh1z.www.mycat.bean;

public class MyCatWorldBean {

    private String num;
    private String username;
    private int gender;
    private String type;
    private String catName;
    private int level;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getCatType() {
        return type;
    }

    public void setCatType(String catType) {
        this.type = catType;
    }

    public String getCatId() {
        return catName;
    }

    public void setCatId(String catId) {
        this.catName = catId;
    }

    public int getCatLevel() {
        return level;
    }

    public void setCatLevel(int catLevel) {
        this.level = catLevel;
    }
}
