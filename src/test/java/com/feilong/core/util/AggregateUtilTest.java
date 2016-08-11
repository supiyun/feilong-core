/*
 * Copyright (C) 2008 feilong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feilong.core.util;

import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.ComparatorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.functors.ComparatorPredicate;
import org.apache.commons.collections4.functors.ComparatorPredicate.Criterion;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feilong.core.bean.ConvertUtil;
import com.feilong.core.util.predicate.BeanPredicate;
import com.feilong.core.util.predicate.BeanPredicateUtil;
import com.feilong.test.User;

import static com.feilong.core.Validator.isNullOrEmpty;
import static com.feilong.core.bean.ConvertUtil.toArray;
import static com.feilong.core.bean.ConvertUtil.toBigDecimal;
import static com.feilong.core.bean.ConvertUtil.toList;

/**
 * 
 * @author <a href="http://feitianbenyue.iteye.com/">feilong</a>
 * @since 1.8.0
 */
public class AggregateUtilTest{

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregateUtilTest.class);

    //*******************AggregateUtil.avg(Collection<User>, String, int)********************************

    /**
     * Test avg.
     */
    @Test
    public void testAvg(){
        List<User> list = toList(//
                        new User(2L),
                        new User(5L),
                        new User(5L));

        assertEquals(new BigDecimal("4.00"), AggregateUtil.avg(list, "id", 2));
    }

    @Test
    public void testAvg1(){
        assertEquals(null, AggregateUtil.avg(null, "id", 2));
    }

    @Test(expected = NullPointerException.class)
    public void testAvg11(){
        User user1 = new User(2L);
        user1.setAge(18);

        User user2 = new User(3L);
        user2.setAge(30);

        AggregateUtil.avg(toList(user1, user2), (String) null, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAvg111(){
        User user1 = new User(2L);
        user1.setAge(18);

        User user2 = new User(3L);
        user2.setAge(30);

        AggregateUtil.avg(toList(user1, user2), "   ", 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAvg1111(){
        User user1 = new User(2L);
        user1.setAge(18);

        User user2 = new User(3L);
        user2.setAge(30);

        AggregateUtil.avg(toList(user1, user2), "", 2);
    }

    //****************AggregateUtil.avg(Collection<User>, String[], int)*******************************

    /**
     * Test avg2.
     */
    @Test
    public void testAvg2(){
        User user1 = new User(2L);
        user1.setAge(18);

        User user2 = new User(3L);
        user2.setAge(30);

        Map<String, BigDecimal> map = AggregateUtil.avg(toList(user1, user2), ConvertUtil.toArray("id", "age"), 2);
        assertThat(map, allOf(hasEntry("id", toBigDecimal("2.50")), hasEntry("age", toBigDecimal("24.00"))));
    }

    @Test
    public void testAvg3(){
        assertEquals(emptyMap(), AggregateUtil.avg(null, ConvertUtil.toArray("id", "age"), 2));
    }

    @Test
    public void testAvg32(){
        assertEquals(emptyMap(), AggregateUtil.avg(toList(), ConvertUtil.toArray("id", "age"), 2));
    }

    @Test(expected = NullPointerException.class)
    public void testAvg4(){
        User user1 = new User(2L);
        user1.setAge(18);

        User user2 = new User(3L);
        user2.setAge(30);

        AggregateUtil.avg(toList(user1, user2), (String[]) null, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAvg44(){
        User user1 = new User(2L);
        user1.setAge(18);

        User user2 = new User(3L);
        user2.setAge(30);

        AggregateUtil.avg(toList(user1, user2), toArray("id", null), 2);
    }

    //***************AggregateUtil.sum(Collection<User>, String)*******************************

    /**
     * Test sum.
     */
    @Test
    public void testSum(){
        List<User> list = toList(//
                        new User(2L),
                        new User(5L),
                        new User(5L));
        assertEquals(new BigDecimal(12L), AggregateUtil.sum(list, "id"));
    }

    @Test
    public void testSum1(){
        assertEquals(null, AggregateUtil.sum(null, "id"));
    }

    @Test(expected = NullPointerException.class)
    public void testSum11(){
        AggregateUtil.sum(toList(new User(2L), new User(5L), new User(5L)), (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSum111(){
        AggregateUtil.sum(toList(new User(2L), new User(5L), new User(5L)), "");
    }

    //**************AggregateUtil.sum(Collection<User>, String, Predicate<User>)*******************************
    /**
     * Test sum4.
     */
    @Test
    public void testSum4(){
        List<User> list = toList(//
                        new User(2L),
                        new User(50L),
                        new User(50L));

        assertEquals(new BigDecimal(100L), AggregateUtil.sum(list, "id", new Predicate<User>(){

            @Override
            public boolean evaluate(User user){
                return user.getId() > 10L;
            }
        }));

        Predicate<Long> predicate = new ComparatorPredicate<Long>(10L, ComparatorUtils.<Long> naturalComparator(), Criterion.LESS);
        BigDecimal sum = AggregateUtil.sum(list, "id", new BeanPredicate<User>("id", predicate));
        assertEquals(new BigDecimal(100L), sum);
    }

    @Test
    public void testSum41(){
        assertEquals(null, AggregateUtil.sum(null, "id", new Predicate<User>(){

            @Override
            public boolean evaluate(User user){
                return user.getId() > 10L;
            }
        }));
    }

    @Test
    public void testSum42(){
        assertEquals(null, AggregateUtil.sum(ConvertUtil.<User> toList(), "id", new Predicate<User>(){

            @Override
            public boolean evaluate(User user){
                return user.getId() > 10L;
            }
        }));
    }

    @Test(expected = NullPointerException.class)
    public void testSum423(){
        assertEquals(null, AggregateUtil.sum(ConvertUtil.<User> toList(), (String) null, new Predicate<User>(){

            @Override
            public boolean evaluate(User user){
                return user.getId() > 10L;
            }
        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSum4231(){
        assertEquals(null, AggregateUtil.sum(ConvertUtil.<User> toList(), "", new Predicate<User>(){

            @Override
            public boolean evaluate(User user){
                return user.getId() > 10L;
            }
        }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSum42311(){
        assertEquals(null, AggregateUtil.sum(ConvertUtil.<User> toList(), " ", new Predicate<User>(){

            @Override
            public boolean evaluate(User user){
                return user.getId() > 10L;
            }
        }));
    }

    @Test
    public void testSum5(){
        List<User> list = toList(//
                        new User(2L),
                        new User(50L),
                        new User(50L));

        assertEquals(null, AggregateUtil.sum(list, "id", new Predicate<User>(){

            @Override
            public boolean evaluate(User user){
                return user.getId() > 100L;
            }
        }));
    }

    //*************AggregateUtil.sum(Collection<User>, String...)*******************************
    /**
     * Test sum2.
     */
    @Test
    public void testSum2(){
        User user1 = new User(2L);
        user1.setAge(18);

        User user2 = new User(3L);
        user2.setAge(30);

        Map<String, BigDecimal> map = AggregateUtil.sum(toList(user1, user2), "id", "age");
        assertThat(map, allOf(hasEntry("id", toBigDecimal(5)), hasEntry("age", toBigDecimal(48))));
    }

    @Test
    public void testSum21(){
        assertEquals(emptyMap(), AggregateUtil.sum(null, "id", "age"));
    }

    @Test
    public void testSum211(){
        assertEquals(emptyMap(), AggregateUtil.sum(toList(), "id", "age"));
    }

    @Test(expected = NullPointerException.class)
    public void testSum2111(){
        User user1 = new User(2L);
        user1.setAge(18);
        AggregateUtil.sum(toList(user1), (String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSum21111(){
        User user1 = new User(2L);
        user1.setAge(18);
        AggregateUtil.sum(toList(user1), "id", (String) null);
    }

    //************AggregateUtil.sum(Collection<User>, String[], Predicate<User>)*****************************

    /**
     * Test sum3.
     */
    @Test
    public void testSum3(){
        User liubei = new User(10L);
        liubei.setName("刘备");
        liubei.setAge(50);

        User guanyu = new User(20L);
        liubei.setName("关羽");
        guanyu.setAge(50);

        User zhangfei = new User(100L);
        zhangfei.setName("张飞");
        zhangfei.setAge(null);

        User zhaoyun = new User((Long) null);
        zhaoyun.setName("赵云");
        zhaoyun.setAge(100);

        List<User> list = toList(liubei, guanyu, zhangfei, zhaoyun);

        Predicate<User> notPredicate = PredicateUtils.notPredicate(BeanPredicateUtil.equalPredicate("name", "张飞"));
        Map<String, BigDecimal> map = AggregateUtil.sum(list, toArray("id", "age"), notPredicate);

        assertThat(map, allOf(hasEntry("id", toBigDecimal(30)), hasEntry("age", toBigDecimal(200))));
    }

    @Test
    public void testSum31(){
        assertEquals(emptyMap(), AggregateUtil.sum(null, toArray("id", "age"), BeanPredicateUtil.equalPredicate("name", "张飞")));
    }

    @Test
    public void testSum311(){
        assertEquals(emptyMap(), AggregateUtil.sum(toList(), toArray("id", "age"), BeanPredicateUtil.equalPredicate("name", "张飞")));
    }

    @Test
    public void testSum3111(){
        User zhangfei = new User(100L);
        zhangfei.setName("张飞");
        zhangfei.setAge(null);

        List<User> list = toList(zhangfei);

        Predicate<User> notPredicate = PredicateUtils.notPredicate(BeanPredicateUtil.equalPredicate("name", "张飞"));
        Map<String, BigDecimal> map = AggregateUtil.sum(list, toArray("id", "age"), notPredicate);

        assertEquals(true, isNullOrEmpty(map));
    }

    //***************AggregateUtil.groupCount(Collection<User>, String)*****************************************************************

    /**
     * Test group count1.
     */
    @Test
    public void testGroupCount1(){
        List<User> list = toList(//
                        new User("张飞"),
                        new User("关羽"),
                        new User("刘备"),
                        new User("刘备"));

        Map<String, Integer> map = AggregateUtil.groupCount(list, "name");
        assertThat(map, allOf(hasEntry("刘备", 2), hasEntry("张飞", 1), hasEntry("关羽", 1)));
    }

    @Test
    public void testGroupCount11(){
        assertEquals(emptyMap(), AggregateUtil.groupCount(null, "name"));
    }

    @Test
    public void testGroupCount111(){
        assertEquals(emptyMap(), AggregateUtil.groupCount(toList(), "name"));
    }

    @Test(expected = NullPointerException.class)
    public void testGroupCount1111(){
        User user1 = new User(2L);
        user1.setAge(18);

        AggregateUtil.groupCount(toList(user1), (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupCount111111(){
        User user1 = new User(2L);
        user1.setAge(18);

        AggregateUtil.groupCount(toList(user1), "   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupCount1111111(){
        User user1 = new User(2L);
        user1.setAge(18);

        AggregateUtil.groupCount(toList(user1), "");
    }

    //********************AggregateUtil.groupCount(Collection<User>, String, Predicate<User>)******************************************************************
    /**
     * Test group count.
     */
    @Test
    public void testGroupCount(){
        List<User> list = toList(//
                        new User("张飞", 20),
                        new User("关羽", 30),
                        new User("赵云", 50),
                        new User("刘备", 40),
                        new User("刘备", 30),
                        new User("赵云", 50));

        Predicate<User> comparatorPredicate = BeanPredicateUtil.comparatorPredicate("age", 30, Criterion.LESS);
        Map<String, Integer> map = AggregateUtil.groupCount(list, "name", comparatorPredicate);
        assertThat(map, allOf(hasEntry("刘备", 1), hasEntry("赵云", 2)));
    }

    @Test
    public void testGroupCount21(){
        assertEquals(emptyMap(), AggregateUtil.groupCount(null, "name", BeanPredicateUtil.comparatorPredicate("age", 30, Criterion.LESS)));
    }

    @Test
    public void testGroupCount211(){
        assertEquals(
                        emptyMap(),
                        AggregateUtil.groupCount(toList(), "name", BeanPredicateUtil.comparatorPredicate("age", 30, Criterion.LESS)));
    }

    @Test(expected = NullPointerException.class)
    public void testGroupCount2111(){
        User user1 = new User(2L);
        user1.setAge(18);

        Predicate<User> comparatorPredicate = BeanPredicateUtil.comparatorPredicate("age", 30, Criterion.LESS);
        AggregateUtil.groupCount(toList(user1), (String) null, comparatorPredicate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupCount211111(){
        User user1 = new User(2L);
        user1.setAge(18);

        Predicate<User> comparatorPredicate = BeanPredicateUtil.comparatorPredicate("age", 30, Criterion.LESS);
        AggregateUtil.groupCount(toList(user1), "   ", comparatorPredicate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupCount2111111(){
        User user1 = new User(2L);
        user1.setAge(18);

        Predicate<User> comparatorPredicate = BeanPredicateUtil.comparatorPredicate("age", 30, Criterion.LESS);
        AggregateUtil.groupCount(toList(user1), "", comparatorPredicate);
    }

    //*************************************************************************************************************

    /**
     * Test get min value.
     */
    @Test
    public void testGetMinValue(){
        Map<String, Integer> map = new HashMap<String, Integer>();

        map.put("a", 3007);
        map.put("b", 3001);
        map.put("c", 3002);
        map.put("d", 3003);
        map.put("e", 3004);
        map.put("f", 3005);
        map.put("g", -1005);

        assertThat(AggregateUtil.getMinValue(map, "a", "b", "d", "g", "m"), is(-1005));
    }

    @Test
    public void testGetMinValue1(){
        assertEquals(null, AggregateUtil.getMinValue(null, "a", "b", "d", "g", "m"));
    }

    @Test
    public void testGetMinValue11(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        assertEquals(null, AggregateUtil.getMinValue(map, "a", "b", "d", "g", "m"));
    }

    @Test
    public void testGetMinValue2(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 3007);
        map.put("b", 3001);
        map.put("c", 3002);
        map.put("d", 3003);
        map.put("e", 3004);
        map.put("f", 3005);

        assertThat(AggregateUtil.getMinValue(map), is(3001));
    }

    @Test
    public void testGetMinValue21(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 3007);
        map.put("b", 3001);
        map.put("c", 3002);
        map.put("d", 3003);
        map.put("e", 3004);
        map.put("f", 3005);

        assertThat(AggregateUtil.getMinValue(map, null), is(3001));
    }

    @Test
    public void testGetMinValue211(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 3007);
        map.put("b", 3001);
        map.put("c", 3002);
        map.put("d", 3003);
        map.put("e", 3004);
        map.put("f", 3005);

        assertThat(AggregateUtil.getMinValue(map, new String[] {}), is(3001));
    }

    @Test
    public void testGetMinValue311(){
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("a", 3007);
        map.put("b", 3001);
        map.put("c", 3002);
        map.put("d", 3003);
        map.put("e", 3004);
        map.put("f", 3005);

        assertEquals(null, AggregateUtil.getMinValue(map, "c1"));
    }
}