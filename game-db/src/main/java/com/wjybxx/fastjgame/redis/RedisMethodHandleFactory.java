/*
 *  Copyright 2019 wjybxx
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to iBn writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.wjybxx.fastjgame.redis;

import redis.clients.jedis.ListPosition;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis客户端工具类(工厂类)
 * 它可以通过扩展客户端来增强功能。
 *
 * @author wjybxx
 * @version 1.0
 * date - 2020/1/9
 * github - https://github.com/hl845740757
 */
public class RedisMethodHandleFactory {

    private RedisMethodHandleFactory() {

    }

    // region string

    public static RedisMethodHandle<Long> incr(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.incr(key));
    }

    public static RedisMethodHandle<Long> incrBy(String key, long increment) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.incrBy(key, increment));
    }

    // endregion

    // region list

    public static RedisMethodHandle<String> lindex(String key, long index) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.lindex(key, index));
    }

    public static RedisMethodHandle<Long> linsert(String key, ListPosition where, String pivot, String value) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.linsert(key, where, pivot, value));
    }

    /**
     * left pop
     * 移除并且返回 key 对应的 list 的第一个元素。
     */
    public static RedisMethodHandle<String> lpop(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.lpop(key));
    }

    /**
     * left push
     * <p>
     * 将所有指定的值插入到存于 key 的列表的头部。
     * 如果 key 不存在，那么在进行 push 操作前会创建一个空列表。
     * 如果 key 对应的值不是一个 list 的话，那么会返回一个错误。
     * <p>
     * 可以使用一个命令把多个元素放入列表，只需在命令末尾加上多个指定的参数。
     * 元素是从最左端的到最右端的、一个接一个被插入到 list 的头部。
     * 所以对于这个命令例子 LPUSH mylist a b c，返回的列表是 c 为第一个元素， b 为第二个元素， a 为第三个元素。
     */
    public static RedisMethodHandle<Long> lpush(String key, String... string) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.lpush(key, string));
    }

    /**
     * left pushx
     * 只有当 key 已经存在并且存着一个 list 的时候，在这个 key 下面的 list 的头部插入 value。
     * 与 LPUSH 相反，当 key 不存在的时候不会进行任何操作。
     */
    public static RedisMethodHandle<Long> lpushx(String key, String... string) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.lpushx(key, string));
    }

    /**
     * right pop
     * 移除并返回存于 key 的 list 的最后一个元素。
     */
    public static RedisMethodHandle<String> rpop(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.rpop(key));
    }

    /**
     * right push
     * 向存于 key 的列表的尾部插入所有指定的值。
     * 如果 key 不存在，那么会创建一个空的列表然后再进行 push 操作。
     * 当 key 保存的不是一个列表时，那么会返回一个错误。
     * <p>
     * 可以使用一个命令把多个元素放入队列，只需要在命令后面指定多个参数。
     * 元素是从左到右一个接一个从列表尾部插入。
     * 比如命令 RPUSH mylist a b c 会返回一个列表，其第一个元素是 a ，第二个元素是 b ，第三个元素是 c。
     */
    public static RedisMethodHandle<Long> rpush(String key, String... string) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.rpush(key, string));
    }

    /**
     * right pushx
     * 当且仅当 key 存在并且是一个列表时，将值 value 插入到列表 key 的表尾。
     * 和 RPUSH 命令相反, 当 key 不存在时，RPUSHX 命令什么也不做。
     */
    public static RedisMethodHandle<Long> rpushx(String key, String... string) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.rpushx(key, string));
    }

    public static RedisMethodHandle<List<String>> lrange(String key, long start, long stop) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.lrange(key, start, stop));
    }

    public static RedisMethodHandle<Long> lrem(String key, long count, String value) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.lrem(key, count, value));
    }

    public static RedisMethodHandle<String> lset(String key, long index, String value) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.lset(key, index, value));
    }

    public static RedisMethodHandle<String> ltrim(String key, long start, long stop) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.ltrim(key, start, stop));
    }

    public static RedisMethodHandle<Long> llen(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.llen(key));
    }

    // endregion

    // region hash

    public static RedisMethodHandle<Boolean> hexists(String key, String field) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hexists(key, field));
    }

    public static RedisMethodHandle<String> hget(String key, String field) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hget(key, field));
    }

    public static RedisMethodHandle<Long> hdel(String key, String... field) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hdel(key, field));
    }

    public static RedisMethodHandle<Long> hincrBy(String key, String field, long value) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hincrBy(key, field, value));
    }

    public static RedisMethodHandle<Long> hset(String key, String field, String value) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hset(key, field, value));
    }

    public static RedisMethodHandle<Long> hsetnx(String key, String field, String value) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hsetnx(key, field, value));
    }

    public static RedisMethodHandle<List<String>> hmget(String key, String... fields) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hmget(key, fields));
    }

    public static RedisMethodHandle<String> hmset(String key, Map<String, String> hash) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hmset(key, hash));
    }

    public static RedisMethodHandle<List<String>> hvals(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hvals(key));
    }

    public static RedisMethodHandle<Long> hlen(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hlen(key));
    }

    public static RedisMethodHandle<Set<String>> hkeys(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hkeys(key));
    }

    public static RedisMethodHandle<Map<String, String>> hgetAll(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.hgetAll(key));
    }
    // endregion

    // region zset
    // 不再建议使用redis做排行榜，使用自己实现的java-zset做排行榜更合适

    public static RedisMethodHandle<Long> zadd(String key, double score, String member) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zadd(key, score, member));
    }

    public static RedisMethodHandle<Long> zadd(String key, double score, String member, ZAddParams params) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zadd(key, score, member, params));
    }

    public static RedisMethodHandle<Long> zadd(String key, Map<String, Double> scoreMembers) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zadd(key, scoreMembers));
    }

    public static RedisMethodHandle<Long> zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zadd(key, scoreMembers, params));
    }

    public static RedisMethodHandle<Long> zcard(String key) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zcard(key));
    }

    public static RedisMethodHandle<Long> zcount(String key, double min, double max) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zcount(key, min, max));
    }

    public static RedisMethodHandle<Double> zincrby(String key, double increment, String member) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zincrby(key, increment, member));
    }

    public static RedisMethodHandle<Double> zincrby(String key, double increment, String member, ZIncrByParams params) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zincrby(key, increment, member, params));
    }

    public static RedisMethodHandle<Long> zrank(String key, String member) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrank(key, member));
    }

    public static RedisMethodHandle<Long> zrevrank(String key, String member) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrevrank(key, member));
    }

    public static RedisMethodHandle<Double> zscore(String key, String member) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zscore(key, member));
    }

    // 总是同时返回分数和member，其实更有意义，老的api着实不好用
    public static RedisMethodHandle<Set<Tuple>> zrangeWithScores(String key, long start, long stop) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrangeWithScores(key, start, stop));
    }

    public static RedisMethodHandle<Set<Tuple>> zrangeByScoreWithScores(String key, double min, double max) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrangeByScoreWithScores(key, min, max));
    }

    public static RedisMethodHandle<Set<Tuple>> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrangeByScoreWithScores(key, min, max, offset, count));
    }

    public static RedisMethodHandle<Set<Tuple>> zrevrangeByScoreWithScores(String key, double max, double min) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrevrangeByScoreWithScores(key, max, min));
    }

    public static RedisMethodHandle<Set<Tuple>> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrevrangeByScoreWithScores(key, max, min, offset, count));
    }

    public static RedisMethodHandle<Set<Tuple>> zrevrangeWithScores(String key, long start, long stop) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrevrangeWithScores(key, start, stop));
    }

    public static RedisMethodHandle<Long> zrem(String key, String... members) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zrem(key, members));
    }

    public static RedisMethodHandle<Long> zremrangeByRank(String key, long start, long stop) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zremrangeByRank(key, start, stop));
    }

    public static RedisMethodHandle<Long> zremrangeByScore(String key, double min, double max) {
        return new DefaultRedisMethodHandle<>(pipeline -> pipeline.zremrangeByScore(key, min, max));
    }

    // endregion
}
