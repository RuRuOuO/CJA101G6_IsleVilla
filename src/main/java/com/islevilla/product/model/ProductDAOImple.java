package com.islevilla.product.model;

import com.islevilla.util.HibernateUtil;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDAOImple implements ProductDAO {
    @Override
    public int insert(Product product){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try{
            session.beginTransaction();
            Integer id = (Integer) session.save(product);
            session.getTransaction().commit();
            return id;
        }
        catch (Exception e){
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return -1;
    };
    @Override
    public int update(Product product){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(product);
            session.getTransaction().commit();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return -1;

    };
    @Override
    public int delete(Integer productId){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            Product product = session.get(Product.class, productId);
            if (product != null) {
                session.delete(product);
            }
            session.getTransaction().commit();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return -1;
    };
    @Override
    public Product findByPK(Integer productId){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            Product product = session.get(Product.class, productId);
            session.getTransaction().commit();
            return product;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return null;
    };
    @Override
    public List<Product> getAll(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Product> list = session.createQuery("from Product ", Product.class).list();
            session.getTransaction().commit();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return null;
    };
}
