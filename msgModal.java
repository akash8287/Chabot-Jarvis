package com.example.jarvis;

public class msgModal {
    private String cnt;

    public String getcnt(String msg){
        return cnt;
    }

    public void setcnt(String cnt){
        this.cnt = cnt;
    }

    public msgModal(String cnt){
        this.cnt = cnt;
    }


}
