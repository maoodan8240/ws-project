package ws.gameServer.features.actor.world.playerIOStatus;

public enum Status {
    Online, // 正常在线状态
    Offline, // 离线状态，但是玩家数据还在内存中
    OutMemory, // 玩家离线状态，且玩家数据不在内存中
}
