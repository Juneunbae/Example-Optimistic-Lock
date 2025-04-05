# Example-OptimisticLock
낙관 락 테스트

## 프로젝트 구성
```
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com.example.locktest
│   │   │   │   ├── product
│   │   │   │   │   ├── Product.java
│   │   │   │   │   ├── ProductRepository.java
│   │   │   │   │   ├── ProductService.java
│   ├── test
│   │   ├── java
│   │   │   ├── com.example.locktest
│   │   │   │   ├── product
│   │   │   │   │   ├── ProductServiceTest.java
```
## Entity Version
```java
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;

    @Version
    private Integer version;
}
```

## ProductService OptimisticLock
```java
@Transactional
public void updateProductPrice(Long id, double price) {
    try {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setPrice(price);

        productRepository.save(product);
    } catch (ObjectOptimisticLockingFailureException e) {
        System.err.println("낙관적 락 충돌 발생!!");
    }
}
```


## 실행 결과
```shell
2025-04-05T17:58:59.299+09:00 DEBUG 17819 --- [optimistic-lock-test] [    Test worker] org.hibernate.SQL                        : 
    insert 
    into
        product
        (name, price, version, id) 
    values
        (?, ?, ?, default)
2025-04-05T17:58:59.303+09:00 TRACE 17819 --- [optimistic-lock-test] [    Test worker] org.hibernate.orm.jdbc.bind              : binding parameter (1:VARCHAR) <- [Product 1]
2025-04-05T17:58:59.303+09:00 TRACE 17819 --- [optimistic-lock-test] [    Test worker] org.hibernate.orm.jdbc.bind              : binding parameter (2:DOUBLE) <- [100.0]
2025-04-05T17:58:59.303+09:00 TRACE 17819 --- [optimistic-lock-test] [    Test worker] org.hibernate.orm.jdbc.bind              : binding parameter (3:INTEGER) <- [0]
2025-04-05T17:58:59.331+09:00 DEBUG 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.SQL                        : 
    select
        p1_0.id,
        p1_0.name,
        p1_0.price,
        p1_0.version 
    from
        product p1_0 
    where
        p1_0.id=?
2025-04-05T17:58:59.331+09:00 DEBUG 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.SQL                        : 
    select
        p1_0.id,
        p1_0.name,
        p1_0.price,
        p1_0.version 
    from
        product p1_0 
    where
        p1_0.id=?
2025-04-05T17:58:59.332+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.orm.jdbc.bind              : binding parameter (1:BIGINT) <- [1]
2025-04-05T17:58:59.332+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.orm.jdbc.bind              : binding parameter (1:BIGINT) <- [1]
2025-04-05T17:58:59.342+09:00 DEBUG 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.SQL                        : 
    update
        product 
    set
        name=?,
        price=?,
        version=? 
    where
        id=? 
        and version=?
2025-04-05T17:58:59.342+09:00 DEBUG 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.SQL                        : 
    update
        product 
    set
        name=?,
        price=?,
        version=? 
    where
        id=? 
        and version=?
2025-04-05T17:58:59.342+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.orm.jdbc.bind              : binding parameter (1:VARCHAR) <- [Product 1]
2025-04-05T17:58:59.342+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.orm.jdbc.bind              : binding parameter (1:VARCHAR) <- [Product 1]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.orm.jdbc.bind              : binding parameter (2:DOUBLE) <- [300.0]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.orm.jdbc.bind              : binding parameter (3:INTEGER) <- [1]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.orm.jdbc.bind              : binding parameter (4:BIGINT) <- [1]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-5] org.hibernate.orm.jdbc.bind              : binding parameter (5:INTEGER) <- [0]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.orm.jdbc.bind              : binding parameter (2:DOUBLE) <- [200.0]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.orm.jdbc.bind              : binding parameter (3:INTEGER) <- [1]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.orm.jdbc.bind              : binding parameter (4:BIGINT) <- [1]
2025-04-05T17:58:59.343+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.orm.jdbc.bind              : binding parameter (5:INTEGER) <- [0]
Exception in thread "Thread-5" org.opentest4j.AssertionFailedError: Expected jakarta.persistence.OptimisticLockException to be thrown, but nothing was thrown.
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:152)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:73)
	at org.junit.jupiter.api.AssertThrows.assertThrows(AssertThrows.java:35)
	at org.junit.jupiter.api.Assertions.assertThrows(Assertions.java:3128)
	at com.example.optimisticlocktest.product.ProductServiceTest.lambda$testOptimisticLocking$2(ProductServiceTest.java:32)
	at java.base/java.lang.Thread.run(Thread.java:1583)
2025-04-05T17:58:59.349+09:00 DEBUG 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.SQL                        : 
    select
        p1_0.id,
        p1_0.name,
        p1_0.price,
        p1_0.version 
    from
        product p1_0 
    where
        p1_0.id=?
2025-04-05T17:58:59.350+09:00 TRACE 17819 --- [optimistic-lock-test] [       Thread-4] org.hibernate.orm.jdbc.bind              : binding parameter (1:BIGINT) <- [1]
Exception in thread "Thread-4" org.springframework.orm.ObjectOptimisticLockingFailureException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [com.example.optimisticlocktest.product.Product#1]
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:325)
	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:244)
	at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:566)
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.processCommit(AbstractPlatformTransactionManager.java:795)
	at org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:758)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(TransactionAspectSupport.java:698)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:416)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:119)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:184)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:727)
	at com.example.optimisticlocktest.product.ProductService$$SpringCGLIB$$0.updateProductPrice(<generated>)
	at com.example.optimisticlocktest.product.ProductServiceTest.lambda$testOptimisticLocking$0(ProductServiceTest.java:28)
	at java.base/java.lang.Thread.run(Thread.java:1583)
Caused by: org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [com.example.optimisticlocktest.product.Product#1]
	at org.hibernate.engine.jdbc.mutation.internal.ModelMutationHelper.identifiedResultsCheck(ModelMutationHelper.java:75)
	at org.hibernate.persister.entity.mutation.UpdateCoordinatorStandard.lambda$doStaticUpdate$9(UpdateCoordinatorStandard.java:785)
	at org.hibernate.engine.jdbc.mutation.internal.ModelMutationHelper.checkResults(ModelMutationHelper.java:50)
	at org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.performNonBatchedMutation(AbstractMutationExecutor.java:141)
	at org.hibernate.engine.jdbc.mutation.internal.MutationExecutorSingleNonBatched.performNonBatchedOperations(MutationExecutorSingleNonBatched.java:55)
	at org.hibernate.engine.jdbc.mutation.internal.AbstractMutationExecutor.execute(AbstractMutationExecutor.java:55)
	at org.hibernate.persister.entity.mutation.UpdateCoordinatorStandard.doStaticUpdate(UpdateCoordinatorStandard.java:781)
	at org.hibernate.persister.entity.mutation.UpdateCoordinatorStandard.performUpdate(UpdateCoordinatorStandard.java:328)
	at org.hibernate.persister.entity.mutation.UpdateCoordinatorStandard.update(UpdateCoordinatorStandard.java:245)
	at org.hibernate.action.internal.EntityUpdateAction.execute(EntityUpdateAction.java:169)
	at org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:644)
	at org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:511)
	at org.hibernate.event.internal.AbstractFlushingEventListener.performExecutions(AbstractFlushingEventListener.java:414)
	at org.hibernate.event.internal.DefaultFlushEventListener.onFlush(DefaultFlushEventListener.java:41)
	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:127)
	at org.hibernate.internal.SessionImpl.doFlush(SessionImpl.java:1429)
	at org.hibernate.internal.SessionImpl.managedFlush(SessionImpl.java:491)
	at org.hibernate.internal.SessionImpl.flushBeforeTransactionCompletion(SessionImpl.java:2354)
	at org.hibernate.internal.SessionImpl.beforeTransactionCompletion(SessionImpl.java:1978)
	at org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl.beforeTransactionCompletion(JdbcCoordinatorImpl.java:439)
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl.beforeCompletionCallback(JdbcResourceLocalTransactionCoordinatorImpl.java:169)
	at org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorImpl$TransactionDriverControlImpl.commit(JdbcResourceLocalTransactionCoordinatorImpl.java:267)
	at org.hibernate.engine.transaction.internal.TransactionImpl.commit(TransactionImpl.java:101)
	at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:562)
	... 10 more
```
