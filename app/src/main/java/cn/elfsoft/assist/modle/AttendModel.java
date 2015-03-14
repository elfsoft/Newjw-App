package cn.elfsoft.assist.modle;

import java.util.List;

public class AttendModel {
    private String footer;
    private List<AttendsRows> rows;
    private int total;

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public List<AttendsRows> getRows() {
        return rows;
    }

    public void setRows(List<AttendsRows> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
