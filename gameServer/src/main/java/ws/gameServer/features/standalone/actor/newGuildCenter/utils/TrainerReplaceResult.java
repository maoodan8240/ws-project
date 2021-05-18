package ws.gameServer.features.standalone.actor.newGuildCenter.utils;

import ws.relationship.topLevelPojos.newGuildCenter.NewGuildTrainer;

import java.io.Serializable;

/**
 * Created by lee on 6/9/17.
 */
public class TrainerReplaceResult implements Serializable {
    private static final long serialVersionUID = 2424165304678556831L;
    private NewGuildTrainer beforeTrainer;
    private NewGuildTrainer Nowtrainer;
    private boolean isBeforeTrainerNull; //训练桩之前是否是空的，如果是空的，beforeTrainer=null

    public TrainerReplaceResult() {
    }

    public TrainerReplaceResult(NewGuildTrainer nowtrainer) {
        Nowtrainer = nowtrainer;
        this.isBeforeTrainerNull = true;
    }

    public TrainerReplaceResult(NewGuildTrainer beforeTrainer, NewGuildTrainer nowtrainer) {
        this.beforeTrainer = beforeTrainer;
        this.isBeforeTrainerNull = false;
        Nowtrainer = nowtrainer;
    }

    public NewGuildTrainer getBeforeTrainer() {
        return beforeTrainer;
    }

    public NewGuildTrainer getNowtrainer() {
        return Nowtrainer;
    }

    public boolean isBeforeTrainerNull() {
        return isBeforeTrainerNull;
    }
}
