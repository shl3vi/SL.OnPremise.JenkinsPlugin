package io.sealights.plugins.sealightsjenkins.entities;

public class ValidationError {
    private String name;
    private String problem;

    public ValidationError(String name, String problem){
        this.name = name;
        this.problem = problem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}