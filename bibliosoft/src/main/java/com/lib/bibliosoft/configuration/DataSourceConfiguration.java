package com.lib.bibliosoft.configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;

/**
 * @Author: 毛文杰
 * @Description: Datasource configuration
 * @Date: Created in 3:07 PM. 9/29/2018
 * @Modify By:
 */
@Configuration//=Spring xml file
@ComponentScan(basePackages = "com.lib.bibliosoft.configuration")
@PropertySource(value = {"classpath:application.yml"}, ignoreResourceNotFound = true)//Match the yml file of the specified path
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfiguration  {

    private String username;
    private String password;
    private String driver_class_name;
    private String url;

    public String getDriver_class_name() {
        return driver_class_name;
    }

    public void setDriver_class_name(String driver_class_name) {
        this.driver_class_name = driver_class_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Bean(destroyMethod = "")
    @Primary
    public DataSource createDataSource() throws PropertyVetoException {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setJdbcUrl(url);
        comboPooledDataSource.setDriverClass(driver_class_name);
        comboPooledDataSource.setUser(username);
        comboPooledDataSource.setPassword(password);
        comboPooledDataSource.setAutoCommitOnClose(false);
        return comboPooledDataSource;
    }

}
