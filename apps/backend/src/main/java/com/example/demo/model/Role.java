package com.example.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@Data
@Getter
@Setter
@Builder
public class Role {

    @Id
    private Integer roleId;
    private AppRole roleName;

}
