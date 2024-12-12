package dev.cxl.iam_service.respository.impl;

import dev.cxl.iam_service.dto.request.UserInformationSearchRequest;
import dev.cxl.iam_service.entity.User;
import dev.cxl.iam_service.entity.UserInformation;
import dev.cxl.iam_service.respository.custom.UserInformationRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInformationRepositoryImpl implements UserInformationRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<UserInformation> search(UserInformationSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql =
                "select u from UserInformation u " + createWhereQuery(request, values) + createOrderQuery(request.getSortBy());
        Query query = entityManager.createQuery(sql, UserInformation.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getPageIndex() - 1) * request.getPageSize());
        query.setMaxResults(request.getPageSize());
        return query.getResultList();
    }

    @Override
    public Long count(UserInformationSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(u) from UserInformation u " + createWhereQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(UserInformationSearchRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        //        sql.append(" left join RoleEntity r on (e.roleId = r.id) ");
        sql.append(" where u.deleted = false");
        if (StringUtils.isNotBlank(request.getKeyword())) {
            sql.append(" and ( lower(u.streetName) like :keyword"
                    + " or lower(u.ward) like :keyword"
                    + " or lower(u.district) like :keyword"
                    + " or lower(u.province) like :keyword) ");
            values.put("keyword", "%" + request.getKeyword().toLowerCase() + "%");
        }
        if (StringUtils.isNotBlank(request.getYearsOfExperience().toString())) {
            sql.append(" and u.yearsOfExperience = :yearsOfExperience ");
            values.put("yearsOfExperience", request);
        }

        return sql.toString();
    }

    public StringBuilder createOrderQuery(String sortBy) {
        StringBuilder hql = new StringBuilder("");
        if (StringUtils.isNotBlank(sortBy)) {
            hql.append(" order by u.").append(sortBy.replace(".", " "));
        } else {
            hql.append(" order by u.dateOfBirth desc ");
        }
        return hql;
    }


}


