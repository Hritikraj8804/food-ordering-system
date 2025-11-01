package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "addresses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType type;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Column(nullable = false, length = 500)
    private String fullAddress;

    @Size(max = 100, message = "Landmark cannot exceed 100 characters")
    @Column(length = 100)
    private String landmark;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String city;

    @NotBlank(message = "Pincode is required")
    @Size(min = 6, max = 6, message = "Pincode must be 6 digits")
    @Column(nullable = false, length = 6)
    private String pincode;

    @Column(columnDefinition = "boolean default false")
    private boolean isDefault;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public AddressType getType() { return type; }
    public void setType(AddressType type) { this.type = type; }
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    @Override
    public String toString() {
        return "Address{id=" + id + ", type=" + type + ", city='" + city + "', pincode='" + pincode + "', isDefault=" + isDefault + "}";
    }
}