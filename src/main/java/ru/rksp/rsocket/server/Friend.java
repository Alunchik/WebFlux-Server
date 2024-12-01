package ru.rksp.rsocket.server;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;


@Table("friendsr")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend implements Serializable {
    @Id
    Long id;
    @Column("name")
    String name;
    @Column("age")
    Integer age;
    @Column("city")
    String city;
}
