package com.speakeasy.model;

/** ゲームの進行段階（フェーズ） */
public enum Phase {
    ORDER_DISTRIBUTION, // 注文配布
    AUCTION,            // オークション
    TRADE,              // 取引
    SERVE,              // 提供
    END                 // 終了
}
