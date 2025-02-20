package com.snowflakes.rednose.entity;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class StampCraft {

    private Member host;

    private CanvasType canvasType;

    private List<Member> members;

    private String[][] stamp;

    private LocalDateTime createdAt;

    protected StampCraft() {
    }

    @Builder
    public StampCraft(Member host, CanvasType canvasType, List<Member> members, String[][] stamp, LocalDateTime createdAt) {
        this.host = host;
        this.canvasType = canvasType;
        this.createdAt = createdAt;
        this.members = new ArrayList<>();
        this.stamp = new String[canvasType.getSize()][canvasType.getSize()];
        Arrays.fill(stamp, "#fff");
    }

    public Member getHost() {
        return host;
    }

    public CanvasType getCanvasType() {
        return canvasType;
    }

    public List<Member> getMembers() {
        return members;
    }

    public String[][] getStamp() {
        return stamp;
    }

    public void paint(int x, int y, String color) {
        if(color.isEmpty()) throw new RuntimeException();
        stamp[x][y] = color;
    }

    public void enter(Member member) {
        members.add(member);
    }

    public void quit(Member member) {
        members.remove(member);
    }

    public boolean memberIsHost(Member member) {
        return host.getId().equals(member.getId());
    }

    public void chooseNewHost() {
        if (!members.isEmpty()) {
            host = members.get(0);
        }
    }

    public boolean hasMembers() {
        return members.size() != 0;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
