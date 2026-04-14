package mate.academy.dao.impl;

import java.util.Optional;
import mate.academy.dao.ShoppingCartDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Dao
public class ShoppingCartDaoImpl implements ShoppingCartDao {
    private final SessionFactory factory = HibernateUtil.getSessionFactory();

    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.save(shoppingCart);
            transaction.commit();
            return shoppingCart;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException(
                    "Can't insert shopping cart", e);
        }
    }

    @Override
    public Optional<ShoppingCart> getByUser(User user) {
        try (Session session = factory.openSession()) {
            Query<ShoppingCart> query = session.createQuery(
                    "from ShoppingCart sc "
                            + "left join fetch sc.tickets "
                            + "where sc.user = :user",
                    ShoppingCart.class
            );
            query.setParameter("user", user);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException(
                    "Can't get shopping cart by user: " + user.getId(), e);
        }
    }

    @Override
    public void update(ShoppingCart shoppingCart) {
        Transaction transaction = null;
        try (Session session = factory.openSession()) {
            transaction = session.beginTransaction();
            session.update(shoppingCart);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException(
                    "Can't update shopping cart", e);
        }
    }
}
