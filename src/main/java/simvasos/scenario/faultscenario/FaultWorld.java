package simvasos.scenario.faultscenario;

import simvasos.scenario.mciresponse.MCIResponseScenario.SoSType;
import simvasos.scenario.mciresponse.MCIResponseWorld;
import simvasos.simulation.component.Message;

import java.util.*;

public class FaultWorld extends MCIResponseWorld {

    private class DelayedMessage {
        Message msg;
        int delay;

        public DelayedMessage(Message msg, int delay) {
            this.msg = msg;
            this.delay = delay;
        }
    }

    private ArrayList<DelayedMessage> delayedMessages = new ArrayList<DelayedMessage>();

    public FaultWorld(SoSType type, int nPatient) {
        super(type, nPatient);
    }

    @Override
    public void reset() {
        if(delayedMessages != null) {
            delayedMessages.clear();
        }
        super.reset();
    }

    @Override
    public void progress(int time) {

        assert time == 1 : "현재 이 함수는 time이 1이라는 전제를 바탕으로 작성되었음";

        // 삭제 대기열과 관련한 코드는 성능을 개선시킬 여지가 있다고 생각됨.

        // 삭제 대기열
        ArrayList<DelayedMessage> mustRemoveMsgs = new ArrayList<DelayedMessage>();
        for(DelayedMessage delayedMsg : delayedMessages) {
            if(delayedMsg.delay == 0) {
                // 삭제 대기열에 추가
                mustRemoveMsgs.add(delayedMsg);

                super.sendMessage(delayedMsg.msg);
            } else {
                delayedMsg.delay--;
            }
        }

        // 삭제 대기열에 넣어둔 delayedMsg 들을 delayedMessages에서 제거
        for(DelayedMessage mustRemoveMsg : mustRemoveMsgs) {
            delayedMessages.remove(mustRemoveMsg);
        }

        super.progress(time);
    }

    @Override
    public void sendMessage(Message msg) {
        // 특정 조건(ex. 확률)을 만족 시키는 경우, delay를 걸어줌
        boolean isDelay = true;
        if(isDelay) {
            // 딜레이 몇 초?
            int delay = 10;
            delayedMessages.add(new DelayedMessage(msg, delay));
        } else {
            super.sendMessage(msg);
        }
    }
}
