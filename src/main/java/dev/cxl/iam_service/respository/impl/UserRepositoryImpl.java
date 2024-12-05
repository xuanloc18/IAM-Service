package dev.cxl.iam_service.respository.impl;

import dev.cxl.iam_service.dto.request.UserSearchRequest;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.respository.UserRespository;
import dev.cxl.iam_service.respository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> search(UserSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select u from User u " + createWhereQuery(request, values) + createOrderQuery(request.getSortBy());
        Query query = entityManager.createQuery(sql, User.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getPageIndex() - 1) * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        return query.getResultList();
    }

    @Override
    public Long count(UserSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from User u " + createWhereQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(UserSearchRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
//        sql.append(" left join RoleEntity r on (e.roleId = r.id) ");
        sql.append(" where u.deleted = false");
        if (StringUtils.isNotBlank(request.getKeyword())) {
            sql.append(
                    " and ( lower(u.userName) like :keyword"
                            + " or lower(u.userMail) like :keyword"
                            + " or lower(u.firstName) like :keyword"
                            + " or lower(u.lastName) like :keyword) ");
            values.put("keyword", "%" + request.getKeyword().toLowerCase() + "%");
        }
        if (StringUtils.isNotBlank(request.getUserName())) {
            sql.append(" and u.userName = :userName ");
            values.put("userName", request.getUserName());
        }
        if (!StringUtils.isEmpty(request.getUserMail())) {
            sql.append(" and u.userMail in :userMail ");
            values.put("userMail", request.getUserMail());
        }
        return sql.toString();
    }

    public StringBuilder createOrderQuery(String sortBy) {
        StringBuilder hql = new StringBuilder(" ");
        if (StringUtils.isNotBlank(sortBy)) {
            hql.append(" order by u.").append(sortBy.replace(".", " "));
        } else {
            hql.append(" order by u.dateOfBirth desc ");
        }
        return hql;
    }
}

