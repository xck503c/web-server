package com.xck.check.reactor;

import java.util.ArrayList;
import java.util.List;

/**
 * 审核事件，记录审核链位置和事件源
 */
public class CheckEvent {

    private Object sourceEvent; //事件源
    private List<EventType> eventTypeChain = new ArrayList<>(); //事件类型，或者说审核链
    private List<Long> longList = new ArrayList<>();
    private int index; //当前事件链处理的位置

    public CheckEvent(Object sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

    public Object getSourceEvent() {
        return sourceEvent;
    }

    public void setSourceEvent(Object sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

    public void addEventTypeChain(EventType eventType) {
        this.eventTypeChain.add(eventType);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EventType nextEvent(){
        if(index >=eventTypeChain.size()){
//            longList.add(System.currentTimeMillis());
//            StringBuffer sb = new StringBuffer();
//            for(int i=0; i<longList.size()-1; i++){
//                sb.append(i).append(": ").append(longList.get(i+1)-longList.get(i)).append(" ");
//            }
//            System.out.println(sb.append("last: ").append(longList.get(longList.size()-1)-longList.get(0)).toString());
            return EventType.none;
        }
//        longList.add(System.currentTimeMillis());
        EventType eventType = eventTypeChain.get(index);
        ++index;
        return eventType;
    }
}
