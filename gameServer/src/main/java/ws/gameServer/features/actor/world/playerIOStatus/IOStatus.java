package ws.gameServer.features.actor.world.playerIOStatus;

public enum IOStatus {
    Ining, // 正在登录
    Inend, // 登录结束 且 成功

    Killing, // 正在被删除
    Killend, // 删除结束

    OfflineOperating, // 离线操作正在进行中

    NULL;//


    /**
     * 是否是中间状态
     *
     * @param ioStatus
     * @return
     */
    public static boolean isIntermediateStatus(IOStatus ioStatus) {
        if (ioStatus == IOStatus.Ining || ioStatus == IOStatus.Killing || ioStatus == IOStatus.OfflineOperating) {
            return true;
        }
        return false;
    }
}