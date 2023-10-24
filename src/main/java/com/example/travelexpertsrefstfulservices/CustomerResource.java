package com.example.travelexpertsrefstfulservices;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import models.Customer;

import java.util.List;

@Path("/customer")
public class CustomerResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getallcustomers")
    public String getallcustomer() {
        EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = managerFactory.createEntityManager();
        Query query = entityManager.createQuery("select c from Customer c");
        List<Customer> list = query.getResultList();

        Gson gson = new Gson();  //gson is updated to be able to convert lists to jsonArrays without needing to use type and typetoken

        return gson.toJson(list);
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getallselectcustomers")
    public String getallSelectcustomer() {
        EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = managerFactory.createEntityManager();
        Query query = entityManager.createQuery("select c from Customer c");
        List<Customer> list = query.getResultList();

        JsonArray jsonArray = new JsonArray();
        for (Customer c : list)
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("customerId", c.getId());
            jsonObject.addProperty("custFirstName", c.getCustFirstName());
            jsonObject.addProperty("custLastName", c.getCustLastName());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }
    //getting the single customer
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getcustomer/{ customerid }")
    public String getcustomer(@PathParam("customerid") int customerId) {
        EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = managerFactory.createEntityManager();
        Customer customer = entityManager.find(Customer.class, customerId);

        Gson gson = new Gson();  //gson is updated to be able to convert lists to jsonArrays without needing to use type and typetoken

        return gson.toJson(customer);
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("postcustomers")
    public String postCustomer(String jsonString) {
        EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = managerFactory.createEntityManager();
        Gson gson = new Gson();
        Customer customer = gson.fromJson(jsonString, Customer.class);
        entityManager.getTransaction().begin();
        Customer mergeCustomer = entityManager.merge(customer);
        String message = null;

        if (mergeCustomer != null)
        {
            entityManager.getTransaction().commit();
             message = "{\"message\": \"Customer updated successfully\" }";
        }
        else
        {
            entityManager.getTransaction().commit();
            message = "{\"message\": \"Customer updated failed\" }";
        }
        entityManager.close();
        return message;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("putcustomers")
    public String putCustomer(String jsonString) {
        EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = managerFactory.createEntityManager();
        Query query = entityManager.createQuery("select c from  Customer c");
        List<Customer> customerList = query.getResultList();
        int listSize = customerList.size();
        Gson gson = new Gson();
        Customer customer = gson.fromJson(jsonString, Customer.class);
        entityManager.getTransaction().begin();
        entityManager.persist(customer);
        String message = null;

        //lookup query size after persist to see if size increased by 1
        query = entityManager.createQuery("select c from  Customer c");
        List<Customer> newcustomerList = query.getResultList();
        int newlistSize = newcustomerList.size();

        if (newlistSize > listSize)
        {
            entityManager.getTransaction().commit();
            message = "{\"message\": \"Customer inserted successfully\" }";
        }
        else
        {
            entityManager.getTransaction().commit();
            message = "{\"message\": \"Customer insert failed\" }";
        }
        entityManager.close();
        return message;
    }
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deletecustomer/{ customerid }")
    public String deletecustomer(@PathParam("customerid") int customerId) {
        EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = managerFactory.createEntityManager();
        Customer customer = entityManager.find(Customer.class, customerId);
        entityManager.getTransaction().begin();
        entityManager.remove(customer);
        String message = null;
        if (!entityManager.contains(customer))
        {
            entityManager.getTransaction().commit();
            message = "{\"message\": \"Customer deleted successfully\" }";
        }
        else
        {
            entityManager.getTransaction().commit();
            message = "{\"message\": \"Unable to delete the customer\" }";
        }
        entityManager.close();
        return message;
    }
}