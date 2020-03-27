package com.bizzan.bitrade.util;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.config.JDBCConfig;
import com.bizzan.bitrade.dto.MemberBonusDTO;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.es.ESUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

/**
 * @Description:
 * @author: GuoShuai
 * @date: create in 13:10 2018/7/2
 * @Modified:
 */
@Slf4j
@Component
public class JDBCUtils {

    @Autowired
    private JDBCConfig jdbcConfig;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    @Autowired
    private ESUtils esUtils;


    public void batchJDBC(List<MemberBonusDTO> paramList) {
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO member_bonus ( member_id, have_time, arrive_time, mem_bouns, coin_id,total) VALUES ( ?, ?, ?, ?, ?, ?)";
        try {

            //Register JDBC driver
            Class.forName(JDBC_DRIVER);
            System.setProperty("jdbc.driver", "com.mysql.jdbc.Driver");

            //Open a connection
            log.info("Connecting to a selected database...");
            conn = DriverManager.getConnection(jdbcConfig.getDbURRL(), jdbcConfig.getUsername(), jdbcConfig.getPassword());
            log.info("Connected database successfully...");


            stmt = conn.prepareStatement(sql);

            conn.setAutoCommit(false);
            for (int i = 0; i < paramList.size(); i++) {
                stmt.setLong(1, paramList.get(i).getMemberId());
                stmt.setString(2, paramList.get(i).getHaveTime());
                stmt.setString(3, paramList.get(i).getArriveTime());
                stmt.setBigDecimal(4, paramList.get(i).getMemBouns());
                stmt.setString(5, paramList.get(i).getCoinId());
                stmt.setBigDecimal(6, paramList.get(i).getTotal());

                stmt.addBatch();
                if (i % 2000 == 0) {
                    stmt.executeBatch();
                    conn.commit();
                }
            }
            stmt.executeBatch();
            conn.commit();
            log.info("Inserted records into the table...");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }

            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("------批量操作SQL：" + sql);
        log.info("------执行时间：" + (endTime - startTime) + "ms");

    }


    //分红到用户钱包
    public void batchJDBCUpdate(List<MemberWallet> paramList, BigDecimal BHBAmount, BigDecimal bounsAmount) {
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "UPDATE member_wallet SET balance=balance+? WHERE coin_id='Ethereum' AND member_id=?";
//        String sql = "UPDATE wealth_info SET bonus_amount=bonus_amount+?,release_amount=release_amount+? WHERE member_id=?";
        try {

            //Register JDBC driver
            Class.forName(JDBC_DRIVER);
            System.setProperty("jdbc.driver", "com.mysql.jdbc.Driver");

            //Open a connection
            log.info("Connecting to a selected database...");
            conn = DriverManager.getConnection(jdbcConfig.getDbURRL(), jdbcConfig.getUsername(), jdbcConfig.getPassword());
            log.info("Connected database successfully...");

            stmt = conn.prepareStatement(sql);

            conn.setAutoCommit(false);
            for (int i = 0; i < paramList.size(); i++) {
                BigDecimal balance = paramList.get(i).getBalance();
                BigDecimal balanceAdd = balance.divide(BHBAmount, 8, BigDecimal.ROUND_DOWN).multiply(bounsAmount);
                stmt.setBigDecimal(1, balanceAdd);
//                stmt.setBigDecimal(2, balanceAdd);
                stmt.setLong(2, paramList.get(i).getMemberId());
                stmt.addBatch();
                if (i % 2000 == 0) {
                    stmt.executeBatch();
                    conn.commit();
                }
            }
            stmt.executeBatch();
            conn.commit();
            log.info("UPDATE records into the table...");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
            log.info(se + "========================");
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
            log.info(e + "========================");
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }

            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("------批量操作SQL：" + sql);
        log.info("------执行时间：" + (endTime - startTime) + "ms");

    }





    public void deleteFromMemberByRelaNameStatus() {
        long startTime = System.currentTimeMillis();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        Statement updateStatement = null;
        try {
            //1.获取Connection
            connection = DriverManager.getConnection(jdbcConfig.getDbURRL(), jdbcConfig.getUsername(), jdbcConfig.getPassword());
            //2.获取Statement
            statement = connection.createStatement();
            updateStatement = connection.createStatement();
            //3.准备Sql
            String sql = " SELECT  inviter_id, count(inviter_id) counts from member WHERE inviter_id is not null and id_number is NULL GROUP BY inviter_id ";

            rs = statement.executeQuery(sql);

            while(rs.next()){
                Long memberId = rs.getLong("inviter_id");
                Long counts = rs.getLong("counts");
                String updateSql = "update member_wallet set to_released=to_released - "+(counts*60 )+" where member_id = "+memberId+ " AND coin_id='BHB' ";
                log.info(">>>>>>更新sql>>>>>"+updateSql);
                updateStatement.executeUpdate(updateSql);
                log.info(">>>>此次更新数据会员id>>"+memberId+">>>金额>>>>"+(counts*60)+">>>>清理会员>>>"+counts);
                log.info("会员:"+memberId+"名下有:"+counts+"被邀请人未实名，扣减BHB数量:"+(counts*60));
                Thread.sleep(10);

            }
            log.info(">>>>>>此次插入时间>>>>"+(System.currentTimeMillis()-startTime));
        //5.处理ResultSet

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (statement != null) {
                    connection.close();
                }

            } catch (SQLException se) {
            }// do nothing
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void synchronization2MemberRegisterWallet(List<Member> members,String coinId) {
        long startTime = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement stmt = null;
        Statement state = null;
        ResultSet rs = null;
        String sql = " INSERT INTO member_wallet( address, balance, frozen_balance, is_lock, member_id, version, coin_id, to_released )" +
                " VALUES ( \"\", 0, 0, 0, ?, 0, ?, 0) ";

        try {

            //Register JDBC driver
            Class.forName(JDBC_DRIVER);
            System.setProperty("jdbc.driver", "com.mysql.jdbc.Driver");

            //Open a connection
            log.info("Connecting to a selected database...");
            conn = DriverManager.getConnection(jdbcConfig.getDbURRL(), jdbcConfig.getUsername(), jdbcConfig.getPassword());
            log.info("Connected database successfully...");


            stmt = conn.prepareStatement(sql);

            state = conn.createStatement();
            String querySql = "select id from member";
            rs = state.executeQuery(querySql);
            conn.setAutoCommit(false);

            int i=0;
            while(rs.next()){

                log.info("sql>>>>"+sql);
                log.info("会员id>>>>>"+rs.getLong("id")+">>>>币种>>>"+coinId);
                stmt.setLong(1, rs.getLong("id"));
                stmt.setString(2, coinId);
                i++;
                stmt.addBatch();
                if (i % 2000 == 0) {
                    stmt.executeBatch();
                    conn.commit();
                }
            }
            stmt.executeBatch();
            conn.commit();
            log.info("Inserted records into the table...");
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }

            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("------批量操作SQL：" + sql);
        log.info("------执行时间：" + (endTime - startTime) + "ms");

    }

//    public static void main(String[] args) {
//        List<MemberWallet> memberWallets = new ArrayList<>();
//         String sql1 = "SELECT * FROM member_transaction WHERE id>=";
//        String sql2 = " AND id<";
//        int total = 10058359;
//        int start = 1;
//        int end= 2000;
//        for (int i = 1; i <= 5030; i++) {
//            if ( end >= total){
//                end = total;
//            }
//            String sql = sql1+start+sql2+end;
//            start = end;
//            end =end+2000;
//            System.out.println(sql);
//        }
//
//    }


}
