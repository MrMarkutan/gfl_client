package com.geeksforless.client.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "steps")
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "action")
    private String action;
    @Column(name = "value_", length = 16384)
    private String value;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    public Step() {
    }

    public Step(Long id, String action, String value, long scenarioId, Scenario scenario) {
        this.id = id;
        this.action = action;
        this.value = value;
        this.scenario = scenario;
    }

    public Step(String action, String value) {
        this.action = action;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }





    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Step step = (Step) o;
        return Objects.equals(id, step.id) && Objects.equals(action, step.action) && Objects.equals(value, step.value) && Objects.equals(scenario, step.scenario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, action, value, scenario);
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", value='" + value + '\'' +
                ", scenario=" + scenario +
                '}';
    }
}