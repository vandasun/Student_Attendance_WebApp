package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "class_types")
@Data
@Accessors(chain = true)
public class ClassType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_type_id_seq")
    @SequenceGenerator(name = "class_type_id_seq", sequenceName = "class_type_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private String name;
}
