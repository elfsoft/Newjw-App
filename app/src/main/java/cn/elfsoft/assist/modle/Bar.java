package cn.elfsoft.assist.modle;

public class Bar {
    private String name;
    private String term;
    private String hidyzm;

    public Bar(String name, String term) {
        super();
        this.name = name;
        this.term = term;
    }

    public Bar() {

    }

    public String getHidyzm() {
        return hidyzm;
    }

    public void setHidyzm(String hidyzm) {
        this.hidyzm = hidyzm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

}
