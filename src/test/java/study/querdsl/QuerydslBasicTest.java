package study.querdsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querdsl.entity.Member;
import study.querdsl.entity.QMember;
import study.querdsl.entity.Team;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;
import static study.querdsl.entity.QMember.*;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    @BeforeEach
    public void before() {
        // queryFactory 이와 같이 초기화
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        // 영속성 컨텍스트 반영하여 쿼리 날리기
        em.flush();
        // 영속성컨텍스트 완전 초기화
        em.clear();

    }

    @Test
    public void startJPQL() {
        // member1 을 찾아라
        String qlString =
                "select m from Member m " +
                "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }


    @Test
    public void startQuerydsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em); // 앤티티 메니저를 같이 넣어줘야함
        // 이를 가지고 Q Member는 변수명에다가 별칭 같은 것을 넣어줘야한다는데...
        // 뒤에서 설명
        // 왜 중요하지 않냐면 앞으로 사용하지 않을 것이라서.. 처음이니깐 ^^
        QMember m = new QMember("m");//어떤 q 맴버인지 구분할려고?, 값을 하나 넣어줘야한다고 함

        // 일반 sql 이랑 비슷해~~
        // 파라미터 바인딩을 위에 하는 것과 다르게
        // 파라미터 바인딩을 안하고 eq
        // 자동으로 prepared state 로 바인딩 자체적으로 해줘
        // sql 인젝션 방어책
        /*
        select
            member0_.member_id as member_i1_1_,
            member0_.age as age2_1_,
            member0_.team_id as team_id4_1_,
            member0_.username as username3_1_
        from
            member member0_
        where
            member0_.username=? -- 요부분으로 파라미터 바인딩
         */
        // 위 JPQL 방식으로는 런타임시에 실행해야 알 수 있는 오류이지만...
        // 아래 querydsl 방식은 컴파일 시점에서 오류를 잡는다!
        // 이게 다시말하지만 강점이라고!!!
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // JPAQueryFactory 는 필드 레벨로 가져가도 무방!
    JPAQueryFactory queryFactory; // 동시성 문제 걱정 안해도 된다!

    // 위 테스트 코드 줄이는 버전
    @Test
    public void startQuerydsl2() {
        // 매번 할때마다 em 넣어도 무방!
        // 필드로 뺴서 매 처리마다 em 주입 무관
        // 알아서 처리 해줌!
//        queryFactory = new JPAQueryFactory(em); // 이것도 before 에다가 해준다
        // 실제 서버 처리에서는 어떻게? - epro 코드를 보자!
        QMember m = new QMember("m");//어떤 q 맴버인지 구분할려고?, 값을 하나 넣어줘야한다고 함
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    // 위 테스트 코드 줄이는 버전
    @Test
    public void startQuerydsl3() {
        // 매번 할때마다 em 넣어도 무방!
        // 필드로 뺴서 매 처리마다 em 주입 무관
        // 알아서 처리 해줌!

//        QMember m = QMember.member;//어떤 q 맴버인지 구분할려고?, 값을 하나 넣어줘야한다고 함
        // 위 변수 보다 아래처럼 Q 클래스에 public static final 변수 직접 임포트하여 쓰는 것을 권장한다!!!
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        // querydsl은 결과적으로 빌더를 통해 jpql 만들어 주는 것
        // 이상태로는 jpql 보고 싶을때는 yml 편집
        /*
            select
                member1
            from
                Member member1 -- alias member1 포인트!!!
            where
                member1.username = ?1
         */// 아래와 같이 변수 m1 대입시
        // QMember m1 = QMember.member;
        // Member findMember = queryFactory
        //        .select(m1)
        //        .from(m1)
        //        .where(member.username.eq("member1"))
        //        .fetchOne();
        // 같은 테이블을 조인하는 경우 이런 경우에 한하여는 이런식으로 변수 직접 생성하여 주입하는 쪽으로 함
        // 그렇지 않은 경우에는 구지 쓸 일이 없다
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

}
