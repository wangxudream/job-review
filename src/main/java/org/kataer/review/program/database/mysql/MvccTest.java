package org.kataer.review.program.database.mysql;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo 完善mvcc
 */
public class MvccTest {
  public static void main(String[] args) {

  }


  @Slf4j
  public static class MysqlInnoDB {
    /**
     * 全局事务id
     */
    private static final AtomicInteger globe_trx_id = new AtomicInteger();
    /**
     * 一行数据
     */
    private static DataLine dataLine;

    private static DataLine undoLog;

    static {
      /**
       * 头插法
       */
//      DataLine dataLine_2 = new DataLine("2", 3, 1, null);
//      DataLine dataLine_1 = new DataLine("3", 2, 1, dataLine_2);
      dataLine = new DataLine("1", 1, 1, null);
    }


    public void update(String data) {
      setTrxTreadLocal();

    }

    public void select() {
      Integer trxId = getTrxTreadLocal();
      DataLine temp = dataLine;
      while (temp != null) {
        if (temp.getDb_trx_id() == 0 || temp.getDb_trx_id() < trxId) {
          log.info("select :{}", JSON.toJSONString(dataLine));
          temp = temp.getDb_roll_point();
        }
      }
    }

    private synchronized void setUndoLog(String data, int trxId) {
      if (undoLog == null) {
        undoLog = new DataLine(data, trxId, dataLine.getDb_row_id(), dataLine);
      } else {
        //todo
      }
    }

    public void begin() {
      setTrxTreadLocal();
    }

    public void commit() {
      removeTrxTreadLocal();
    }

    private void rollback() {

    }

    private Integer getTrxTreadLocal() {
      return TrxTreadLocal.getThreadLocal();
    }

    private Integer setTrxTreadLocal() {
      if (TrxTreadLocal.getThreadLocal() == null) {
        TrxTreadLocal.setThreadLocal(globe_trx_id.incrementAndGet());
      }
      return TrxTreadLocal.getThreadLocal();
    }

    private void removeTrxTreadLocal() {
      TrxTreadLocal.remove();
    }

    @Data
    @AllArgsConstructor
    public static class DataLine {
      //    private Integer primary_key;
      private String data;
      /**
       * 修改/插入当前数据的事务id
       */
      private int db_trx_id;
      /**
       * 隐含的主键
       */
      private int db_row_id;
      /**
       * 回滚指针(上一个版本的数据的地址)
       */
      private DataLine db_roll_point;
    }

    @Data
    public static class ReadView {
      /**
       * 当前系统下活跃的事务id
       */
      private Set<Integer> trx_id;
      /**
       * 创建read_view时活动的最大事务id
       */
      private int low_limit_id;
      /**
       * 创建read_view活动的最小事务id
       */
      private int up_limit_id;
      /**
       * 创建当前read_view的事务id
       */
      private int creator_trx_id;
    }

    enum IsoEnum {
      RC,
      RR;
    }

    public static class TrxTreadLocal {
      private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

      public static void setThreadLocal(Integer value) {
        threadLocal.set(value);
      }

      public static Integer getThreadLocal() {
        return threadLocal.get();
      }

      public static void remove() {
        threadLocal.remove();
      }
    }
  }
}
