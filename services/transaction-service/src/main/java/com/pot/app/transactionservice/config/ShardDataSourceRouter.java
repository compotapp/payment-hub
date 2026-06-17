//package com.pot.app.transactionservice.config;
//
//import com.pot.app.shared.sharding.ShardRouter;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//public class ShardDataSourceRouter extends AbstractRoutingDataSource {
//
//    private static final ThreadLocal<String> currentShard = new ThreadLocal<>();
//
//    public static void setShard(String userId) {
//        int shardIndex = ShardRouter.getTransactionShardIndex(userId);
//        currentShard.set("shard" + shardIndex);
//    }
//
//    public static void clear() {
//        currentShard.remove();
//    }
//
//    @Override
//    protected Object determineCurrentLookupKey() {
//        return currentShard.get();
//    }
//}