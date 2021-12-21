package com.my_web.community.community_demo.entity;

public class Page {
    int current = 1;
    int offset;
    int limit = 10;
    String path;
    int rows_num;
    int total_page;
    int from;
    int to;

    public int getTotal_page() {
        int total_page;
        if (rows_num % limit == 0)
            total_page = rows_num / limit;
        else
            total_page = rows_num / limit + 1;
        return total_page;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getOffset() {
        return (current - 1) * limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRows_num() {
        return rows_num;
    }

    public void setRows_num(int rows_num) {
        this.rows_num = rows_num;
    }

    public int getFrom(){
        return current < 3 ? 1:current - 2;
    }

    public int getTo(){
        int total = getTotal_page();
        return current > total - 2 ? total: current + 2;
    }
}