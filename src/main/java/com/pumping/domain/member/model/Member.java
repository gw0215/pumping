package com.pumping.domain.member.model;

import com.pumping.domain.routine.model.Routine;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String email;

    private String password;

    private String profileImage;

    @OneToMany(mappedBy = "member")
    private List<Routine> routines = new ArrayList<>();

    public Member(String nickname, String email, String password, String profileImage) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    
}
