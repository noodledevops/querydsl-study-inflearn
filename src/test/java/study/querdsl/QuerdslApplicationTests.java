package study.querdsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querdsl.entity.Hello;
import study.querdsl.entity.QHello;

import javax.persistence.EntityManager;

@SpringBootTest	
@Transactional 	// 기본적으로 다 롤백
@Commit		 	// 공부할때 테스트는 남기지만 실전에서는 메모리에서만 돌려서 확인하는게 좋다
class QuerdslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		// 이렇게 뉴써서 하지말고
//		QHello qHello = new QHello("h");
		// 이런식으로 쓴다
		QHello qHello = QHello.hello; // Q 안에 만들어 둔것이 있기때문에 이걸 씀

		Hello result = query
				.selectFrom(qHello)
				.fetchOne();

		Assertions.assertThat(result).isEqualTo(hello);
		Assertions.assertThat(result.getId()).isEqualTo(hello.getId());
	}

}
