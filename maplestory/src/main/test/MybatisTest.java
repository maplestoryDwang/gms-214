import com.dwang.MapperProxy;
import com.dwang.config.MyBatisSessionFactory;
import com.dwang.dao.Account;
import com.dwang.dao.TraceKingUserInfo;
import com.dwang.data.mapper.AccountMapper;
import com.dwang.data.mapper.TraceKingUserInfoMapper;
import org.apache.ibatis.session.SqlSession;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName MybatisTest.java
 * @Description 测试mybatis使用
 * @createTime 2025-11-23 20:22
 */

public class MybatisTest {

    public static void main(String[] args) {

//        try (SqlSession session = MyBatisSessionFactory.getSession()) {
//            TraceKingUserInfoMapper  mapper = session.getMapper(TraceKingUserInfoMapper .class);
//            TraceKingUserInfo traceKingUserInfo = mapper.selectById(1);
//
//        }


        // 动态代理
        TraceKingUserInfoMapper mapper = MapperProxy.create(TraceKingUserInfoMapper.class);
        TraceKingUserInfo traceKingUserInfo = mapper.selectById(1);
        System.out.println(traceKingUserInfo);


    }
}
