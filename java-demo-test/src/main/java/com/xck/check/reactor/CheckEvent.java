package com.xck.check.reactor;

/**
 * 审核事件
 */
public class CheckEvent {

    private Object sourceEvent; //事件源
    private EventType[] eventTypeChain; //事件类型，或者说审核链
    private int index; //当前事件链处理的位置

    public Object getSourceEvent() {
        return sourceEvent;
    }

    public void setSourceEvent(Object sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

    public EventType[] getEventTypeChain() {
        return eventTypeChain;
    }

    public void setEventTypeChain(EventType[] eventTypeChain) {
        this.eventTypeChain = eventTypeChain;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public EventType nextEvent(){
        if(++index >=eventTypeChain.length){
            return EventType.none;
        }
        return eventTypeChain[++index];
    }
}
