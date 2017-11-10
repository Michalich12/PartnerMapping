package com.partnermapping.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="Customer")
@JsonIgnoreProperties
public class Customer implements Serializable{
    private int id;
    private String fullName;
    private BigDecimal balance;
    private Boolean status;
    private String login;
    @JsonIgnore
    private String pass;

    public Customer() {
    }

    public Customer(int customerId, String login, String password) {
        this.id = customerId;
        this.login = login;
        this.pass = password;
    }

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "FullName")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Basic
    @Column(name = "Balance")
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Basic
    @Column(name = "Status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Basic
    @Column(name = "Login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    @Column(name = "Pass")
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;

        Customer customer = (Customer) o;

        if (id != customer.id) return false;
        if (fullName != null ? !fullName.equals(customer.fullName) : customer.fullName != null) return false;
        if (balance != null ? !balance.equals(customer.balance) : customer.balance != null) return false;
        if (status != null ? !status.equals(customer.status) : customer.status != null) return false;
        if (login != null ? !login.equals(customer.login) : customer.login != null) return false;
        return pass != null ? pass.equals(customer.pass) : customer.pass == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", balance=" + balance +
                ", status=" + status +
                ", login='" + login + '\'' +
                '}';
    }
}
