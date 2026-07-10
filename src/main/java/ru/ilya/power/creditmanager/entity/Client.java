package ru.ilya.power.creditmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthdate;

    @Column(name = "passport_number", nullable = false, unique = true, length = 20)
    private String passportNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "status", nullable = false)
    private ClientStatus status;
}