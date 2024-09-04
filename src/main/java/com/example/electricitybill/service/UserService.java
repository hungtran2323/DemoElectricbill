import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User updateUserUsingQueryDSL(String phoneNumber, User updatedUser) {
        QUser qUser = QUser.user;

        queryFactory.update(qUser)
                .where(qUser.phoneNumber.eq(phoneNumber))
                .set(qUser.email, updatedUser.getEmail())
                .set(qUser.password, passwordEncoder.encode(updatedUser.getPassword()))
                .execute();

        return queryFactory.selectFrom(qUser)
                .where(qUser.phoneNumber.eq(phoneNumber))
                .fetchOne();
    }

    @Transactional
    public boolean deleteUserUsingQueryDSL(int id) {
        QUser qUser = QUser.user;

        long deletedCount = queryFactory.delete(qUser)
                .where(qUser.id.eq(id))
                .execute();

        return deletedCount > 0;
    }
    public User findUserByPhoneNumberUsingQueryDSL(String phoneNumber) {
        QUser qUser = QUser.user;

        return queryFactory.selectFrom(qUser)
                .where(qUser.phoneNumber.eq(phoneNumber))
                .fetchOne();
    }

    public List<BillInfo> getAllBillInfoUsingQueryDSL() {
        QBillInfo qBillInfo = QBillInfo.billInfo;

        return queryFactory.selectFrom(qBillInfo)
                .fetch();
    }


}