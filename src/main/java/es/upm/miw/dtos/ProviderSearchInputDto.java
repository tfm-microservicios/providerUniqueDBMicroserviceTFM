package es.upm.miw.dtos;

import javax.validation.constraints.NotNull;

public class ProviderSearchInputDto {

    private String company;

    private String nif;

    private String email;

    private String phone;

    @NotNull
    private boolean active;

    public ProviderSearchInputDto(boolean active) {
        this.active = active;
    }

    public ProviderSearchInputDto(String company, String nif, String email, String phone, @NotNull boolean active) {
        this.company = company;
        this.nif = nif;
        this.email = email;
        this.phone = phone;
        this.active = active;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}