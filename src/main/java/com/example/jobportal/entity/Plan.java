package com.example.jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer jobLimit;

    @Column(nullable = false)
    private Boolean active = true;
}
