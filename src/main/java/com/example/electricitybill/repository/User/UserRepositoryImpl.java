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
    public User updateUserUsingQueryDSL(String username, User updatedUser) {
        QUser qUser = QUser.user;

        List<User> existingUsers = queryFactory.selectFrom(qUser)
                .where(qUser.username.eq(username))
                .fetch();

        if (existingUsers.isEmpty()) {
            return null;
        }

        User userToUpdate = existingUsers.get(0);

        queryFactory.update(qUser)
                .where(qUser.id.eq(userToUpdate.getId()))
                .set(qUser.username, updatedUser.getUsername())
                .set(qUser.email, updatedUser.getEmail())
                .set(qUser.registrationDate, updatedUser.getRegistrationDate())
                .execute();

        return queryFactory.selectFrom(qUser)
                .where(qUser.id.eq(userToUpdate.getId()))
                .fetchOne();
    }

    @Override
    public boolean deleteUserUsingQueryDSL(int  id) {
        QUser qUser = QUser.user;

        long deletedCount = queryFactory.delete(qUser)
                .where(qUser.id.eq(id))
                .execute();

        return deletedCount > 0;
    }

    @Override
    public List<User> getUserByUsernameUsingQueryDSL(String username) {
        QUser qUser = QUser.user;

        return queryFactory.selectFrom(qUser)
                .where(qUser.username.eq(username))
                .fetch();
    }

    @Override
    public List<User> getUserByUsernameAndDateUsingQueryDSL(String username, Date registrationDate) {
        QUser qUser = QUser.user;

        return queryFactory.selectFrom(qUser)
                .where(qUser.username.eq(username)
                        .and(qUser.registrationDate.eq(registrationDate)))
                .fetch();
    }
}