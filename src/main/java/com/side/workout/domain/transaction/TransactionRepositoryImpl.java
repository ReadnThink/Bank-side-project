package com.side.workout.domain.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

interface Dao{
    List<Transaction> findTransactionList(@Param("accountId") Long accountId, @Param("category") String category, @Param("page") Integer page);

}

// impl은 꼭 붙여줘야 하고, TransactionRepository 가 앞에 붙어야 한다.
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao{

    private final EntityManager em;

    @Override
    public List<Transaction> findTransactionList(Long accountId, String category, Integer page) {
        // 동적쿼리 (category 값을 가지고 동적쿼리 = DEPOSIT, WITHDRAW, ALL)

        // JPQL 문법
        String sql = "";
        sql += "select t from Transaction t ";

        if(category.equals("WITHDRAW")){
            sql += "join fetch t.withdrawAccount wa ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        }else if(category.equals("DEPOSIT")){
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId";
        }else{ // category = ALL
            sql += "left join fetch t.withdrawAccount wa ";
            sql += "left join fetch t.depositAccount da ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);

        if(category.equals("WITHDRAW")){
            query = query.setParameter("withdrawAccountId", accountId);
        }else if(category.equals("DEPOSIT")){
            query = query.setParameter("depositAccountId", accountId);
        }else{
            query = query.setParameter("withdrawAccountId", accountId);
            query = query.setParameter("depositAccountId", accountId);
        }
        query.setFirstResult(page * 5); //
        query.setMaxResults(5);
        return query.getResultList();
    }
}
