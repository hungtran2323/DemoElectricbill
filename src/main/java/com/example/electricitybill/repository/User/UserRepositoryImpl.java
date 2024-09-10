package com.example.electricitybill.repository.User;

import com.example.electricitybill.model.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public User updateUserUsingQueryDSL(String email, User updatedUser) {
        QUser qUser = QUser.user;

        List<User> existingUsers = queryFactory.selectFrom(qUser)
                .where(qUser.email.eq(email))
                .fetch();

        if (existingUsers.isEmpty()) {
            return null;
        }

        User userToUpdate = existingUsers.get(0);

        queryFactory.update(qUser)
                .where(qUser.id.eq(userToUpdate.getId()))
                .set(qUser.email, updatedUser.getEmail())
                .set(qUser.password, updatedUser.getPassword())
                .execute();

        return queryFactory.selectFrom(qUser)
                .where(qUser.id.eq(userToUpdate.getId()))
                .fetchOne();
    }

    @Override
    public boolean deleteUserUsingQueryDSL(int id) {
        QUser qUser = QUser.user;

        long deletedCount = queryFactory.delete(qUser)
                .where(qUser.id.eq(id))
                .execute();

        return deletedCount > 0;
    }

    @Override
    public List<User> getUserByUsernameUsingQueryDSL(String email) {
        QUser qUser = QUser.user;

        return queryFactory.selectFrom(qUser)
                .where(qUser.email.eq(email))
                .fetch();
    }

    @Override
    public List<User> getUserByUsernameAndDateUsingQueryDSL(String email, entryDate entryDate) {
        QUser qUser = QUser.user;

        return queryFactory.selectFrom(qUser)
                .where(qUser.email.eq(email)
                        .and(qUser.entryDate.eq(entryDate)))
                .fetch();
    }
}