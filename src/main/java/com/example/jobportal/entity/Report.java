package com.example.jobportal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "reports",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"seeker_id", "recruiter_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seeker_id", nullable = false)
    private Seeker seeker;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter recruiter;
}