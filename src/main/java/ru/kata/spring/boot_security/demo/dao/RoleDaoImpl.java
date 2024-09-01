package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;

@Repository
public class RoleDaoImpl implements RoleDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role findRole(Long id) {
        return entityManager.find(Role.class, id);

    }

    @Override
    public Set<Role> rolesSet() {
        return new HashSet<>(entityManager.createQuery(
                "SELECT r FROM Role r").getResultList());
    }

    @Override
    public void add(Role role) {
        entityManager.merge(role);
    }
}
