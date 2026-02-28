/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pnh.configs;

import java.util.Properties;
import javax.sql.DataSource;
import static org.hibernate.cfg.JdbcSettings.DIALECT;
import static org.hibernate.cfg.JdbcSettings.SHOW_SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

/**
 *
 * @author ADMIN
 */
@Configuration
@PropertySource("classpath:databases.properties")
@PropertySource(value = "classpath:databases-local.properties", ignoreResourceNotFound = true)
public class HibernateConfigs {
    
    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setPackagesToScan(new String[]{
            "com.pnh.pojo"
        });
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    /** Ưu tiên: env (Railway), rồi -D, rồi properties file. */
    private String getProp(String key) {
        String v = System.getProperty(key);
        if (v != null && !v.isBlank()) return v;
        return env.getProperty(key);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String url, user, pass;
        // Railway MySQL: đọc từ MYSQLHOST, MYSQLUSER... (không cần -D, tránh & trong shell)
        String host = System.getenv("MYSQLHOST");
        if (host != null && !host.isBlank()) {
            String port = System.getenv("MYSQLPORT");
            if (port == null || port.isBlank()) port = "3306";
            String db = System.getenv("MYSQL_DATABASE_OVERRIDE");
            if (db == null || db.isBlank()) db = System.getenv("MYSQLDATABASE");
            if (db == null || db.isBlank()) db = "railway";
            url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?allowPublicKeyRetrieval=true&useSSL=true&serverTimezone=UTC";
            user = System.getenv("MYSQLUSER");
            pass = System.getenv("MYSQLPASSWORD");
        } else {
            url = getProp("hibernate.connection.url");
            user = getProp("hibernate.connection.username");
            pass = getProp("hibernate.connection.password");
        }
        dataSource.setDriverClassName(getProp("hibernate.connection.driverClass"));
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass != null ? pass : "");
        return dataSource;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put(DIALECT, env.getProperty("hibernate.dialect"));
        props.put(SHOW_SQL, env.getProperty("hibernate.showSql"));
        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager
                = new HibernateTransactionManager();
        transactionManager.setSessionFactory(
                getSessionFactory().getObject());
        return transactionManager;
    }
}
