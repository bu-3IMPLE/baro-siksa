package kr.ac.baekseok.java_project.model;

/**
 * 맛집 데이터 모델
 */
public class Restaurant {
    private int id;
    private String name;
    private float rating;
    private String openingHours;
    private String address;
    private String addressDetail;
    private boolean isLocalFavorite;
    private String[] menuItems;
    private int[] menuPrices;

    public Restaurant(int id, String name, float rating, String openingHours) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.openingHours = openingHours;
    }

    public Restaurant(int id, String name, float rating, String openingHours,
                      String address, String addressDetail,
                      boolean isLocalFavorite,
                      String[] menuItems, int[] menuPrices) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.openingHours = openingHours;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isLocalFavorite = isLocalFavorite;
        this.menuItems = menuItems;
        this.menuPrices = menuPrices;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public float getRating() { return rating; }
    public String getOpeningHours() { return openingHours; }
    public String getAddress() { return address; }
    public String getAddressDetail() { return addressDetail; }
    public boolean isLocalFavorite() { return isLocalFavorite; }
    public String[] getMenuItems() { return menuItems; }
    public int[] getMenuPrices() { return menuPrices; }
}
