package com.pumping.domain.member.model;

import com.pumping.domain.routine.model.Routine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    private String profileImagePath;

    private String fcmToken;

    private boolean deleted = false;

    @OneToMany(mappedBy = "member")
    private List<Routine> routines = new ArrayList<>();

    public Member(String nickname, String email, String password, String profileImagePath) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImagePath = profileImagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void updateMemberProfileImage(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public void deleteMember() {
        this.deleted = true;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
