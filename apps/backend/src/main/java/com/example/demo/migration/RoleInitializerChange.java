package com.example.demo.migration;

import com.example.demo.model.AppRole;
import com.example.demo.model.Role;
import io.mongock.api.annotations.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ChangeUnit(id="role-initializer", order = "001", author = "some_random_guy")
public class RoleInitializerChange {
    private final MongoTemplate mongoTemplate;
    private final String ROLES_COLLECTION_NAME = "roles";

    public RoleInitializerChange(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeExecution
    public void beforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.createCollection(ROLES_COLLECTION_NAME);
    }

    /** This is the method with the migration code **/
    @Execution
    public void changeSet() {
        List<Role> rolesObjects = IntStream.range(0, AppRole.values().length)
                .mapToObj(i -> getRole(i, AppRole.values()[i]))
                .collect(Collectors.toList());
        mongoTemplate.insertAll(rolesObjects);
    }


    @RollbackBeforeExecution
    public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection(ROLES_COLLECTION_NAME);
    }

    /**
     This method is mandatory even when transactions are enabled.
     They are used in the undo operation and any other scenario where transactions are not an option.
     However, note that when transactions are available and Mongock need to rollback, this method is ignored.
     **/
    @RollbackExecution
    public void rollbackExecution(MongoTemplate mongoTemplate) {
        mongoTemplate.findAll(Role.class, ROLES_COLLECTION_NAME).forEach(mongoTemplate::remove);
    }

    private static Role getRole(int id, AppRole role) {
        return Role.builder().roleId(id).roleName(role).build();
    }
}
