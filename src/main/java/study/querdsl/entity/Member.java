package study.querdsl.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 앤티티로서 사용하기 위해 필요, 접근 지정자 protected 지정
@ToString(of = {"id", "username", "age"}) // 연관관계 필드 주의
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this(username, 0);
    }

    public Member(String username, int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if (team != null) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        // 팀도 변경하고
        this.team = team;
        // 팀에 연관되어 있는 나도 변경함
        team.getMembers().add(this);
    }
}
