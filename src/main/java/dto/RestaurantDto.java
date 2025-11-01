package dto;

public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    private String cuisineType;
    private Long hotelOwnerId;
    private String hotelOwnerName;

    public RestaurantDto() {}

    public RestaurantDto(Long id, String name, String address, String cuisineType, Long hotelOwnerId, String hotelOwnerName) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.cuisineType = cuisineType;
        this.hotelOwnerId = hotelOwnerId;
        this.hotelOwnerName = hotelOwnerName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
    public Long getHotelOwnerId() { return hotelOwnerId; }
    public void setHotelOwnerId(Long hotelOwnerId) { this.hotelOwnerId = hotelOwnerId; }
    public String getHotelOwnerName() { return hotelOwnerName; }
    public void setHotelOwnerName(String hotelOwnerName) { this.hotelOwnerName = hotelOwnerName; }
}